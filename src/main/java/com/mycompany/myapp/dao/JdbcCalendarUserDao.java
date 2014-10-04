package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;



import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;

@Repository
public class JdbcCalendarUserDao implements CalendarUserDao {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	// --- constructors ---
	public JdbcCalendarUserDao() {

	}

	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	// --- CalendarUserDao methods ---
	//CalendarUser용 위한 CalendarUser용 RowMapper 생성
	private RowMapper<CalendarUser> userMapper =
			new RowMapper<CalendarUser>() {
		public CalendarUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			CalendarUser user = new CalendarUser();
			user.setId(Integer.parseInt(rs.getString("id")) );
			user.setEmail(rs.getString("email"));
			user.setPassword(rs.getString("password"));
			user.setName(rs.getString("name"));
			return user;
		}
	};
	
	// 템플릿을 활용한 getUser 리팩토링
	@Override
	public CalendarUser getUser (int id) {
		//sql_query = "select * from calendar_users where email like ?";
		return this.jdbcTemplate.queryForObject("select * from calendar_users where id = ?", 
				new Object[] {id} ,this.userMapper);
	}
	/*
	public CalendarUser getUser(int id){
		Connection c;
		CalendarUser user = new CalendarUser();
		
		try {
			c = dataSource.getConnection();


			PreparedStatement ps = c.prepareStatement( "select * from calendar_users where id = ?");
			ps.setString(1, Integer.toString(id));

			ResultSet rs = ps.executeQuery();
			rs.next();

			user.setId(Integer.parseInt(rs.getString("id")) );
			user.setEmail(rs.getString("email"));
			user.setPassword(rs.getString("password"));
			user.setName(rs.getString("name"));

			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	*/

	// 템플릿을 화용한 findUserByEmail 리팩토링
	@Override
	public CalendarUser findUserByEmail (String email) {
		String sql_query;
		sql_query = "select * from calendar_users where email like ?";
		return this.jdbcTemplate.queryForObject("select * from calendar_users where email like ?", 
				new Object[] {email} ,this.userMapper);
	}
	/*
	@Override
	public CalendarUser findUserByEmail(String email) {
		
		Connection c;
		CalendarUser user = new CalendarUser();
		try {
			c = dataSource.getConnection();

			String sql_query;
			if(email == null)
				sql_query = "select * from calendar_users";
			else
				sql_query = "select * from calendar_users where email like '"+email+"'";
			PreparedStatement ps;

			ps = c.prepareStatement(sql_query);

			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				
				user.setId(Integer.parseInt(rs.getString("id")) );
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setName(rs.getString("name"));
				
			}
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	
	}*/

	//템플릿을 황요한 findUsersByEmaild 리팩토링
	@Override
	public List<CalendarUser> findUsersByEmail (String email) {
		String sql_query;
		if(email == null)
			sql_query = "select * from calendar_users";
		else
			sql_query = "select * from calendar_users where email like '%"+email+"%'";
		return this.jdbcTemplate.query(sql_query, this.userMapper);
	}
	
	/*
	@Override
	public List<CalendarUser> findUsersByEmail(String email) {
		List<CalendarUser> calendarUsers = new ArrayList<CalendarUser>();
		Connection c;
		try {
			c = dataSource.getConnection();

			String sql_query;
			if(email == null)
				sql_query = "select * from calendar_users";
			else
				sql_query = "select * from calendar_users where email like '%"+email+"%'";
			PreparedStatement ps;

			ps = c.prepareStatement(sql_query);

			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				CalendarUser user = new CalendarUser();
				user.setId(Integer.parseInt(rs.getString("id")) );
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setName(rs.getString("name"));

				calendarUsers.add(user);
			}
			rs.close();
			ps.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	return calendarUsers;
	}*/
	
	//템플릿 황요 CreateUser 리팩토링
	public int createUser(final CalendarUser userToAdd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();		
		//public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("insert into calendar_users(email, password, name) "
						+ "values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setString(1, userToAdd.getEmail());
				ps.setString(2, userToAdd.getPassword());
				ps.setString(3, userToAdd.getName());
				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}

	/*
	@Override
	public int createUser(final CalendarUser userToAdd){
		Connection c;
		int generatedId = 0; 
		try {
			c = dataSource.getConnection();

			PreparedStatement ps = c.prepareStatement( "insert into calendar_users(email, password, name) values(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, userToAdd.getEmail());
			ps.setString(2, userToAdd.getPassword());
			ps.setString(3, userToAdd.getName());

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();

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
	
	//템플릿응 활용한 dellteAll 리팩토링
	@Override
	public void deleteAll() {
		// Assignment 2
		this.jdbcTemplate.update("delete from calendar_users");
		/*
		Connection c; 
		try {
			c = dataSource.getConnection();
			PreparedStatement ps = c.prepareStatement("delete from calendar_users");
			ps.executeUpdate();
			
			ps.close();
			c.close();
		} catch (SQLException e) {
			System.out.println("캘리더 유저 데이터 삭제 오류 "+e.getMessage());
		}*/		
	}
}