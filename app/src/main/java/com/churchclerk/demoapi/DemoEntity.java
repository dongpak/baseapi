/**
 * 
 */
package com.churchclerk.demoapi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/**
 * 
 * @author dongp
 *
 */
@Entity
@Table(name="test")
public class DemoEntity extends Demo {

	@Column(name="active")
	@Override
	public boolean isActive() {
		return super.isActive();
	}

	@Id
	@Column(name="id")
	@Override
	public String getId() {
		return super.getId();
	}

	@Column(name="data")
	@Override
	public String getTestData() {
		return super.getTestData();
	}

	@Override
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	public String getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Override
	public String getUpdatedBy() {
		return super.getUpdatedBy();
	}
}
