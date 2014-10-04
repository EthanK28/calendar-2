package com.mycompany.myapp.dao;

import java.security.Timestamp;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="../applicationContext.xml")

/*
 	** 학번: 2008160006
 	** 이름: 강은석
 */

public class DaoJUnitTest {
	@Autowired
	private CalendarUserDao calendarUserDao;	
	
	@Autowired
	private EventDao eventDao;
	
	private CalendarUser[] calendarUsers;
	private Event[] events; 
	
	//Setup 코드, 초기값 삽입
	@Before
	public void setUp() {
		calendarUsers = new CalendarUser[3];
		events = new Event[3];
		//기존 등록된 유저 삭제 
		this.calendarUserDao.deleteAll();
		
		//기존 등록된 이벤트 삭제 
		this.eventDao.deleteAll();
		
		//디폴트 유저 등록
		CalendarUser setupCreateUser1 = new CalendarUser("user1@example.com","user1","User1");
		CalendarUser setupCreateUser2 = new CalendarUser("admin1@example.com","admin1","Admin");
		CalendarUser setupCreateUser3 = new CalendarUser("user2@example.com","user2","User2");
		
		
		int setupCreatedUserId1 = calendarUserDao.createUser(setupCreateUser1);
		setupCreateUser1.setId(setupCreatedUserId1); calendarUsers[0] = setupCreateUser1; 
		int setupCreatedUserId2 = calendarUserDao.createUser(setupCreateUser2);
		setupCreateUser2.setId(setupCreatedUserId2); calendarUsers[1] = setupCreateUser2;
		int setupCreatedUserId3 = calendarUserDao.createUser(setupCreateUser3);
		setupCreateUser3.setId(setupCreatedUserId3); calendarUsers[2] = setupCreateUser3;

		//디폴트 이벤트 등록
		String event1Ts = "2013-10-04 20:30:00";		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(event1Ts));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		Event setupCreateEvent1 = new Event(cal,"Birthday Party", "This is going to be a great birthday", 
				calendarUserDao.getUser(setupCreatedUserId1), calendarUserDao.getUser(setupCreatedUserId2));;
		eventDao.createEvent(setupCreateEvent1);
		events[0] = setupCreateEvent1;
		
		//2번째 이벤트 등록	
		String event2Ts = "2013-12-23 13:00:00";
		try {
			cal.setTime(sdf.parse(event2Ts));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Event setupCreateEvent2 = new Event(cal,"Conference Call","Call with the client",calendarUserDao.getUser(setupCreatedUserId3), 
				calendarUserDao.getUser(setupCreatedUserId1));// 3,1));
		eventDao.createEvent(setupCreateEvent2);
		events[1] = setupCreateEvent2;
		
		//3번째 event 등록
		String event3Ts = "2014-01-23 11:30:00";
		try {
			cal.setTime(sdf.parse(event3Ts));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Event setupCreateEvent3 = new Event(cal,"Lunch","Eating lunch together",calendarUserDao.getUser(setupCreatedUserId2),
				calendarUserDao.getUser(setupCreatedUserId3));
		eventDao.createEvent(setupCreateEvent3);
		events[2] = setupCreateEvent3;
		
		
		/*
		createEvent2.setWhen(Calendar.getInstance());
		createEvent2.setSummary("event2 - summary");
		createEvent2.setDescription("event2 - description");
		createEvent2.setOwner(calendarUserDao.getUser(3));
		createEvent2.setAttendee(calendarUserDao.getUser(1));		
		createEvent1Id = eventDao.createEvent(createEvent1);
		createEvent2Id = eventDao.createEvent(createEvent2);
		 */
		
		
		/* [참고]
		insert into calendar_users(`id`,`email`,`password`,`name`) values (1,'user1@example.com','user1','User1');
		insert into calendar_users(`id`,`email`,`password`,`name`) values (2,'admin1@example.com','admin1','Admin');
		insert into calendar_users(`id`,`email`,`password`,`name`) values (3,'user2@example.com','user2','User1');

		insert into events (`id`,`when`,`summary`,`description`,`owner`,`attendee`) values (100,'2013-10-04 20:30:00','Birthday Party','This is going to be a great birthday',1,2);
		insert into events (`id`,`when`,`summary`,`description`,`owner`,`attendee`) values (101,'2013-12-23 13:00:00','Conference Call','Call with the client',3,1);
		insert into events (`id`,`when`,`summary`,`description`,`owner`,`attendee`) values (102,'2014-01-23 11:30:00','Lunch','Eating lunch together',2,3);
		*/
		
		// 1. SQL 코드에 존재하는 3개의 CalendarUser와 Event 각각을 Fixture로서 인스턴스 변수 calendarUsers와 events에 등록하고 DB에도 저장한다. 
		// [주의 1] 모든 id 값은 입력하지 않고 DB에서 자동으로 생성되게 만든다. 
		// [주의 2] Calendar를 생성할 때에는 DB에서 자동으로 생성된 id 값을 받아 내어서 Events를 만들 때 owner와 attendee 값으로 활용한다.
		
	}
	
	
	@Test
	public void createCalendarUserAndCompare() {
		// 2. 새로운 CalendarUser 2명 등록 및 각각 id 추출하고, 추출된 id와 함께 새로운 CalendarUser 2명을 DB에서 가져와 (getUser 메소드 사용) 
		//    방금 등록된 2명의 사용자와 내용 (이메일, 이름, 패스워드)이 일치하는 지 비교
		System.out.println("\n2.------------------------------------------------");
		CalendarUser createUser1 = new CalendarUser();
		CalendarUser createUser2 = new CalendarUser();
		
		int createUser1Id;
		int createUser2Id;
		
		createUser1.setEmail("createUser1@spring.book");
		createUser1.setPassword("createUser1");
		createUser1.setName("createUser1");
		createUser2.setEmail("createUser2@spring.book");
		createUser2.setPassword("createUser2");
		createUser2.setName("createUser2");
		
		createUser1Id = calendarUserDao.createUser(createUser1);
		createUser2Id = calendarUserDao.createUser(createUser2);
		
		System.out.println("createUser1 Id: "+createUser1Id);
		System.out.println("createUser2 Id: "+createUser2Id);
		
		CalendarUser getCreateUser1 = calendarUserDao.getUser(createUser1Id);
		
		assertThat(createUser1.getName(),is(getCreateUser1.getName()));
		assertThat(createUser1.getEmail(),is(getCreateUser1.getEmail()));
		assertThat(createUser1.getPassword(),is(getCreateUser1.getPassword()));
		
		CalendarUser getCreateUser2 = calendarUserDao.getUser(createUser2Id);
		
		assertThat(createUser2.getName(),is(getCreateUser2.getName()));
		assertThat(createUser2.getEmail(),is(getCreateUser2.getEmail()));
		assertThat(createUser2.getPassword(),is(getCreateUser2.getPassword()));
		System.out.println("2번테스트 성공");
		System.out.println("------------------------------------------------");
		
		
	}
	
	@Test
	public void createEventUserAndCompare() {
		// 3. 새로운 Event 2개 등록 및 각각 id 추출하고, 추출된 id와 함께 새로운 Event 2개를 DB에서 가져와 (getEvent 메소드 사용) 
		//    방금 추가한 2개의 이벤트와 내용 (summary, description, owner, attendee)이 일치하는 지 비교
		// [주의 1] when은 비교하지 않아도 좋다.
		// [주의 2] owner와 attendee는 @Before에서 미리 등록해 놓은 3명의 CalendarUser 중에서 임의의 것을 골라 활용한다.
		System.out.println("\n3.------------------------------------------------");
		
		Event createEvent1 = new Event();
		Event createEvent2 = new Event();
		int createdEvent1Id;
		int createdEvent2Id;
		
		//2개의 event 등록을 위한 객체 생성및 갑설정 -> 데이터베이스 등록
		createEvent1.setWhen(Calendar.getInstance());
		
		createEvent1.setSummary("event1 - summary");
		createEvent1.setDescription("event1 - description");
		createEvent1.setOwner(calendarUserDao.getUser(calendarUsers[0].getId())); //1
		createEvent1.setAttendee(calendarUserDao.getUser(calendarUsers[1].getId())); //2
		System.out.println("ID: "+calendarUsers[0].getId());
		createdEvent1Id = eventDao.createEvent(createEvent1);
		
		
		createEvent2.setWhen(Calendar.getInstance());
		createEvent2.setSummary("event2 - summary");
		createEvent2.setDescription("event2 - description");
		createEvent2.setOwner(calendarUserDao.getUser(calendarUsers[2].getId())); //3
		createEvent2.setAttendee(calendarUserDao.getUser(calendarUsers[0].getId())); //1
		createdEvent2Id = eventDao.createEvent(createEvent2);
		
		Event getCreateEvent1 = eventDao.getEvent(createdEvent1Id);
		//System.out.println("getCreatEvent1 아이디: "+getCreateEvent1.getId());
		Event getCreateEvent2 = eventDao.getEvent(createdEvent2Id);
		
		//삽입한 값과 추출한 값이 맞는지 대조
		assertThat(createEvent1.getSummary(),is(getCreateEvent1.getSummary()));
		assertThat(createEvent1.getDescription(),is(getCreateEvent1.getDescription()));
		assertThat(createEvent1.getOwner(),is(getCreateEvent1.getOwner()));
		assertThat(createEvent1.getAttendee(),is(getCreateEvent1.getAttendee()));
		
		assertThat(createEvent2.getSummary(),is(getCreateEvent2.getSummary()));
		assertThat(createEvent2.getDescription(),is(getCreateEvent2.getDescription()));
		assertThat(createEvent2.getOwner(),is(getCreateEvent2.getOwner()));
		assertThat(createEvent2.getAttendee(),is(getCreateEvent2.getAttendee())); 
		System.out.println("3번테스트 성공\n");
		
	}
	
	@Test
	public void getAllEvent() {
		// 4. 모든 Events를 가져오는 eventDao.getEvents()가 올바로 동작하는 지 (총 3개를 가지고 오는지) 확인하는 테스트 코드 작성  
		// [주의] fixture로 등록된 3개의 이벤트들에 대한 테스트
		System.out.println("\n4.------------------------------------------------");
		
		//리스트로 모든이벤트를 가져온후 대입시킨다. 
		List<Event> getEventsList = eventDao.getEvents();
		assertThat(getEventsList.size(),is(3));
		for(int i = 0; i < 3; i++){
			//이벤트 추출한 각각의 값이 미리 설정된 값과 일치하는지 비교
			assertThat(getEventsList.get(i).getSummary(),is(events[i].getSummary()));
			assertThat(getEventsList.get(i).getDescription(),is(events[i].getDescription()));
			assertThat(getEventsList.get(i).getOwner(),is(events[i].getOwner()));
			assertThat(getEventsList.get(i).getAttendee(),is(events[i].getAttendee()));
			System.out.println((i+1)+" 번째값 일치 확인");
		}
		System.out.println("4번테스트 성공\n");	
		
	}
	
	
	@Test
	public void getEvent() {
		// 5. owner ID가 3인 Event에 대해 findForOwner가 올바로 동작하는 지 확인하는 테스트 코드 작성  
		// [주의] fixture로 등록된 3개의 이벤트들에 대해서 owner ID가 3인 것인 1개의 이벤트뿐임
		
		// ID가 생성시마다 바뀌므로 고정된 ID가 3으로는 테스트가 불가능하여 3번째로 등록된 유저의 ID로 테스트를 하였다.
		System.out.println("\n5.------------------------------------------------");
	
		String email3 = "user2@example.com";
		//CalendarUser getEventUser3 = calendarUserDao.findUserByEmail(email3);
		assertThat(calendarUsers[2].getId(), 
				is(eventDao.findForOwner(calendarUserDao.findUserByEmail(email3).getId()).get(0).getOwner().getId()));
		
		System.out.println("5번테스트 성공\n");
		
		//insert into calendar_users(`id`,`email`,`password`,`name`) values (2,'admin1@example.com','admin1','Admin');
		//insert into calendar_users(`id`,`email`,`password`,`name`) values (3,'user2@example.com',
	}
	
	
	@Test
	public void getOneUserByEmail() {
		// 6. email이 'user1@example.com'인 CalendarUser가 존재함을 확인하는 테스트 코드 작성 
		// [주의] public CalendarUser findUserByEmail(String email)를 테스트 하는 코드
		
		//findyUserBy 메소드로 해당 메일값 검색후 메일값 추출하여 일치하는지 비교.
		System.out.println("\n6.------------------------------------------------");
		assertThat("user1@example.com", is(calendarUserDao.findUserByEmail("user1@example.com").getEmail()));
		System.out.println("6번문제 테스트 완료\n");		
		
	}
	
	
	@Test
	public void getTwoUserByEmail() {
		// 7. partialEmail이 'user'인 CalendarUser가 2명임을 확인하는 테스크 코드 작성
		// [주의] public List<CalendarUser> findUsersByEmail(String partialEmail)를 테스트 하는 코드
		System.out.println("\n7.------------------------------------------------");
		
		//findUsersByEmail 메소드로 user 메일에 user가 들어가는 모든 값을 불러온후 기존 저장된값과 비교하여 일치하면 카운트가 증가하게하였다.
		//user가들어가는 곳은 두곳뿐이니 count가 2로 값이 일치하여 테스트에 성공하였다.
		List<CalendarUser> list = calendarUserDao.findUsersByEmail("user");
		int count = 0;
		System.out.println("User가 들어가는 아이디 수: "+list.size());
		
		for (int i = 0; i < calendarUsers.length;i++ ){
			for (int j = 0; j < list.size(); j++){
				if(calendarUsers[i].getId().equals(list.get(j).getId())) {count = count+1;}
			}
		}
		
		assertThat(count, is(2));
		System.out.println("7번 테스트 성공");
		System.out.println(count);
	}
}
