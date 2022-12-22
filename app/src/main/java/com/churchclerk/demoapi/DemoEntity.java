/**
 * 
 */
package com.churchclerk.demoapi;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


/**
 * 
 * @author dongp
 *
 */
@Entity
@Table(name="demo")
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DemoEntity extends Demo {

	@Id
	@Column(name="id")
	@Override
	public String getId() {
		return super.getId();
	}

	@Column(name="active")
	@Override
	public boolean isActive() {
		return super.isActive();
	}

	@Column(name="testdata")
	@Override
	public String getTestData() {
		return super.getTestData();
	}

//	@Column(name = "created_by")
//	@CreatedBy
//	@Override
//	public String getCreatedBy() {
//		return super.getCreatedBy();
//	}
//
//	@Column(name = "created_date")
//	@CreatedDate
//	@Override
//	public Date getCreatedDate() {
//		return super.getCreatedDate();
//	}
//
//	@Column(name = "updated_by")
//	@LastModifiedBy
//	@Override
//	public String getUpdatedBy() {
//		return super.getUpdatedBy();
//	}
//
//	@Column(name = "updated_date")
//	@LastModifiedDate
//	@Override
//	public Date getUpdatedDate() {
//		return super.getUpdatedDate();
//	}
}
