package com.yosep.restful.accounts;

import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	
	private String email;
	
	
	private String password;
	
	@ElementCollection(fetch = FetchType.EAGER) // 여러개의 enum을 가질 수 있기 때문에
	@Enumerated(value = EnumType.STRING) // 
	private Set<AccountRole> roles;
}

// 수정사항
