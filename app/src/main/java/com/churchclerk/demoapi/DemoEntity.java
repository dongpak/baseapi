/**
 * 
 */
package com.churchclerk.demoapi;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


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
	public UUID getId() {
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

	@Column(name = "created_by")
	@Override
	public String getCreatedBy() {
		return super.getCreatedBy();
	}

	@Column(name = "created_date")
	@Override
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Column(name = "updated_by")
	@Override
	public String getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Column(name = "updated_date")
	@Override
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}
}
