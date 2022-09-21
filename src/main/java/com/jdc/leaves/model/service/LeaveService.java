package com.jdc.leaves.model.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdc.leaves.model.dto.input.LeaveForm;
import com.jdc.leaves.model.dto.output.LeaveListVO;
import com.jdc.leaves.model.dto.output.LeaveSummaryVO;

@Service
public class LeaveService {

	private NamedParameterJdbcTemplate template;
	private SimpleJdbcInsert leavesInsert;
	private SimpleJdbcInsert daysInsert;
	
	private static final String SELECT_PROJECTION_SQL = """
			select l.apply_date applyDate,c.id classId,
			c.teacher_id teacherId,ta.name teacher,l.start_date startDate,
			l.student_id studentId,sa.name student,
			s.phone studentPhone,l.days days,l.reason reason
			from leaves l join student s on l.student_id = s.id 
			join account sa on s.id = sa.id
			join classes c on l.classes_id = c.id
			join teacher t on c.teacher_id = t.id
			join account ta on t.id = ta.id
			join leaves_day ld on ld.leaves_apply_date = l.apply_date
			and ld.leaves_classes_id = l.classes_id and ld.leaves_student_id =l.student_id
			""";
	
	private static final String LEAVE_COUNT_SQL = """
			select count(leave_date) from leaves_day 
			where leave_date = :target and leaves_classes_id = :classId
			""";

	@Autowired
	private ClassService classService;

	public LeaveService(DataSource dataSource) {
		template = new NamedParameterJdbcTemplate(dataSource);
		leavesInsert = new SimpleJdbcInsert(dataSource);
		leavesInsert.setTableName("leaves");
		leavesInsert.setColumnNames(List.of(
				"apply_date","classes_id","student_id","start_date","days","reason"
				));
		daysInsert = new SimpleJdbcInsert(dataSource);
		daysInsert.setTableName("leaves_day");
		daysInsert.setColumnNames(List.of(
					"leave_date","leaves_apply_date","leaves_classes_id","leaves_student_id"
				));
		
	}

	public List<LeaveListVO> search(Optional<Integer> classId, Optional<LocalDate> from,
			Optional<LocalDate> to) {
		
		var username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		var where = new StringBuffer(SELECT_PROJECTION_SQL);
		var param = new HashMap<String, Object>();
		
		where.append(" where sa.email =:email or c.id =:classId");
		param.put("email", username);
		param.put("classId", classId.orElse(null));
		
		where.append(from.map(a -> {
			param.put("from", Date.valueOf(a));
			return " and l.start_date >= :from";
		}).orElse(""));

		where.append(to.map(a -> {
			param.put("to", Date.valueOf(a));
			return " and l.start_date <= :to";
		}).orElse(""));
		
		
		return template.query(
				where.toString(), 
				param,
				new BeanPropertyRowMapper<>(LeaveListVO.class));
	

	}

	@Transactional
	public void save(LeaveForm form) {
		
//		insert leaves
		leavesInsert.execute(Map.of(
				"classes_id",form.getClassId(),
				"student_id",form.getStudent(),
				"apply_date",Date.valueOf(form.getApplyDate()),
				"start_date",Date.valueOf(form.getStartDate()),
				"days",form.getDays(),
				"reason",form.getReason()
				));
		
//		insert leaves_day
		
		daysInsert.execute(Map.of(
				"leave_date",Date.valueOf(form.getStartDate()),
				"leaves_apply_date",Date.valueOf(form.getApplyDate()),
				"leaves_classes_id",form.getClassId(),
				"leaves_student_id",form.getStudent()
				));
		
	}

	
	public List<LeaveSummaryVO> searchSummary(Optional<LocalDate> target) {

		// Find Classes
		var classes = classService.search(Optional.ofNullable(null), Optional.ofNullable(null),
				Optional.ofNullable(null));
		
		var result = classes.stream().map(LeaveSummaryVO::new).toList();
		
		for(var vo : result) {
			vo.setLeaves(findLeavesForClass(vo.getClassId(), target.orElse(LocalDate.now())));
		}

		return result;
	}

	private long findLeavesForClass(int classId, LocalDate date) {
		return template.queryForObject(LEAVE_COUNT_SQL, 
				Map.of("classId", classId, "target", Date.valueOf(date)), Long.class);
	}

}