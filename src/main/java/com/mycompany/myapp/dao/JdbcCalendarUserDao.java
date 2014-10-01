package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.mycompany.myapp.domain.CalendarUser;

@Repository
public class JdbcCalendarUserDao implements CalendarUserDao {

	private DataSource dataSource;

	// --- constructors ---
	public JdbcCalendarUserDao() {

	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	// --- CalendarUserDao methods ---
	@Override
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

	@Override
	public CalendarUser findUserByEmail(String email) {
		return null;
	}

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
		}
		return calendarUsers;
	}

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
	}
	
	@Override
	public void deleteAll() {
		// Assignment 2
		Connection c; 
		try {
			c = dataSource.getConnection();
			PreparedStatement ps = c.prepareStatement("delete from calendar_users");
			ps.executeUpdate();
			
			ps.close();
			c.close();
		} catch (SQLException e) {
			System.out.println("캘리더 유저 데이터 삭제 오류 "+e.getMessage());
		}

		
	}
}