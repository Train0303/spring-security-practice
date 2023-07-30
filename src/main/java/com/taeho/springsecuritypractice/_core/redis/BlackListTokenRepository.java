package com.taeho.springsecuritypractice._core.redis;

import org.springframework.data.repository.CrudRepository;

public interface BlackListTokenRepository extends CrudRepository<BlackListToken, String> {

}
