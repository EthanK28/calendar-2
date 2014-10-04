package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;


@Repository
public class JdbcEventDao implements EventDao {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	

	// --- constructors ---
	public JdbcEventDao() { 
	}

	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	// --- EventService ---
	//JdbcEventDao RowMapper
	private RowMapper<Event> userMapper =
			new RowMapper<Event>() {
		public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
			ApplicationContext context = new GenericXmlApplicationContext("com/mycompany/myapp/applicationContext.xml");;
			CalendarUserDao calendarUserDao = context.getBean("calendarUserDao", JdbcCalendarUserDao.class);
			Event event = new Event();
			event.setId(rs.getInt("id"));
			Calendar when = Calendar.getInstance();
			when.setTimeInMillis(rs.getTimestamp("when").getTime());
			event.setWhen(when);
			event.setSummary(rs.getString("summary"));
			event.setDescription(rs.getString("description"));
			CalendarUser owner = calendarUserDao.getUser(rs.getInt("owner"));
			event.setOwner(owner);
			CalendarUser attendee = calendarUserDao.getUser(rs.getInt("attendee"));
			event.setAttendee(attendee);
			return event;
		}
	};
	
	
	//원하는 이벤트 가져오기
	//public Event getEvent(int eventId) {
	//	return this.jdbcTemplate.queryForObject("select * from events where id = ?", new Object[] {eventId}, this.userMapper);		
	//}
	
	//템플릿으로 getEvent 리팩토링
	@Override
	public Event getEvent (int eventId) {
		String sql_query;
		sql_query = "select * from events where id = ?";
		return this.jdbcTemplate.queryForObject(sql_query, new Object[] {eventId} ,this.userMapper);
	}
	
	/*
	public Event getEvent(int eventId) {
		ApplicationContext context = new GenericXmlApplicationContext("com/mycompany/myapp/applicationContext.xml");;
		CalendarUserDao calendarUserDao = context.getBean("calendarUserDao", JdbcCalendarUserDao.class);

		Event event = new Event();

		Connection c;
		try {
			c = dataSource.getConnection();


			PreparedStatement ps = c.prepareStatement( "select * from events where id = ?");
			ps.setString(1, Integer.toString(eventId));

			ResultSet rs = ps.executeQuery();
			rs.next();

			event.setId(Integer.parseInt(rs.getString("id")));
			 
			Calendar when = Calendar.getInstance();
			when.setTimeInMillis(rs.getTimestamp("when").getTime());
			event.setWhen(when);
			event.setSummary(rs.getString("summary"));
			event.setDescription(rs.getString("description"));
			CalendarUser owner = calendarUserDao.getUser(rs.getInt("owner"));
			event.setOwner(owner);
			CalendarUser attendee = calendarUserDao.getUser(rs.getInt("attendee"));
			event.setAttendee(attendee);

			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return event;
	}*/
	
	//템플릿으로 createEvent 리팩토링
	public int createEvent(final Event event) {
		KeyHolder keyHolder = new GeneratedKeyHolder();		
		//public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("insert into events(`when`, summary, description, owner, attendee) "
						+ "values(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				
				Timestamp timestamp = new Timestamp(event.getWhen().getTimeInMillis()); 
				ps.setTimestamp(1, timestamp);
				ps.setString(2, event.getSummary());
				ps.setString(3, event.getDescription());
				ps.setInt(4, event.getOwner().getId());
				ps.setInt(5, event.getAttendee().getId());
				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
	
	/*
	@Override 
	public int createEvent(final Event event) {
		Connection c;
		int generatedId = 0; 
		try {
			c = dataSource.getConnection();

			PreparedStatement ps = c.prepareStatement( "insert into events(`when`, summary, description, owner, attendee) values(?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
			
			Timestamp timestamp = new Timestamp(event.getWhen().getTimeInMillis()); 
			
			ps.setTimestamp(1, timestamp);
			ps.setString(2, event.getSummary());
			ps.setString(3, event.getDescription());
			ps.setInt(4, event.getOwner().getId());
			ps.setInt(5, event.getAttendee().getId());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			
			//rs.next();
			
			if(rs.next())
			{
				generatedId = rs.getInt(1);
			}			
			
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generatedId;
	}*/
	
	//템플릿으로 findForOwner 리팩토링
	@Override
	public List<Event> findForOwner (int ownerUserId) {
		String sql_query;
		sql_query = "select * from events where owner ="+ownerUserId;
		return this.jdbcTemplate.query(sql_query, this.userMapper);
	}
	
	/*
	public List<Event> findForOwner(int ownerUserId) {
		// Assignment 2
		ApplicationContext context = new GenericXmlApplicationContext("com/mycompany/myapp/applicationContext.xml");;
		CalendarUserDao calendarUserDao = context.getBean("calendarUserDao", JdbcCalendarUserDao.class);
		
		Event event = new Event();
		List<Event> list = new ArrayList<Event>();

		Connection c;
		try {
			c = dataSource.getConnection();


			PreparedStatement ps = c.prepareStatement( "select * from events where owner = ?");
			ps.setInt(1, ownerUserId);

			ResultSet rs = ps.executeQuery();
			//rs.next();
			while (rs.next()){
				event.setId(Integer.parseInt(rs.getString("id")));			 
				Calendar when = Calendar.getInstance();
				when.setTimeInMillis(rs.getTimestamp("when").getTime());
				event.setWhen(when);
				event.setSummary(rs.getString("summary"));
				event.setDescription(rs.getString("description"));
				CalendarUser owner = calendarUserDao.getUser(rs.getInt("owner"));
				event.setOwner(owner);
				CalendarUser attendee = calendarUserDao.getUser(rs.getInt("attendee"));
				event.setAttendee(attendee);
				list.add(event);
			
			}		

			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}*/

	//템플릿으로 getEvents 리팩토링
	@Override
	public List<Event> getEvents () {
		String sql_query;
		sql_query = "select * from events";
		return this.jdbcTemplate.query(sql_query, this.userMapper);
	}
	/*
	public List<Event> getEvents(){
		ApplicationContext context = new GenericXmlApplicationContext("com/mycompany/myapp/applicationContext.xml");;

		CalendarUserDao calendarUserDao = context.getBean("calendarUserDao", JdbcCalendarUserDao.class);

		List<Event> list = new ArrayList<Event>();

		Connection c;
		try {
			c = dataSource.getConnection();


			PreparedStatement ps = c.prepareStatement( "select * from events");

			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				Event event = new Event();
				event.setId(Integer.parseInt(rs.getString("id")));
				Calendar when = Calendar.getInstance();
				when.setTimeInMillis(rs.getTimestamp("when").getTime());
				event.setWhen(when);
				event.setSummary(rs.getString("summary"));
				event.setDescription(rs.getString("description"));
				CalendarUser owner = calendarUserDao.getUser(rs.getInt("owner"));
				event.setOwner(owner);
				CalendarUser attendee = calendarUserDao.getUser(rs.getInt("attendee"));
				event.setAttendee(attendee);

				list.add(event);
			}
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	*/
	//템플릿으로 deleteAll 리팩토링
	@Override
	public void deleteAll() {
		// Assignment 2
		this.jdbcTemplate.update("delete from events");		
		
	/*
		Connection c; 
		try {
			c = dataSource.getConnection();
			PreparedStatement ps = c.prepareStatement("delete from events");
			ps.executeUpdate();
			
			ps.close();
			c.close();
		} catch (SQLException e) {
			System.out.println("이벤트 데이터 삭제 오류 "+e.getMessage());
		}*/
	}
}
