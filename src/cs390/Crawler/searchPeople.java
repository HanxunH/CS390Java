package cs390.Crawler;

import org.hibernate.Session;

/**
 * Created by LemonBear on 11/6/16.
 */
public class searchPeople {
	private String firstName;
	private String LastName;
	private String position;
	private String img_url;
	private String homepage_url;
	private String email_url;
	private String phone_number;
	private String office;
	private int people_id;

	public void save(){
		Session session = null;
		try {
			session = DBConnectionManager.getSession();
			session.beginTransaction();
			session.saveOrUpdate(this);
			session.getTransaction().commit();
		}catch(Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		session.close();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getHomepage_url() {
		return homepage_url;
	}

	public void setHomepage_url(String homepage_url) {
		this.homepage_url = homepage_url;
	}

	public String getEmail_url() {
		return email_url;
	}

	public void setEmail_url(String email_url) {
		this.email_url = email_url;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {this.phone_number = phone_number;
	}

	public int getPeople_id() {
		return people_id;
	}

	public void setPeople_id(int people_id) {
		this.people_id = people_id;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}
}
