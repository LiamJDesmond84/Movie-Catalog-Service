package com.liam.moviecatalogservice.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.liam.moviecatalogservice.models.Rating;
import com.liam.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Service
public class UserRatingInfo {
	
	@Autowired
	private RestTemplate restTemplate;
	
	// Extracted method
	@HystrixCommand(fallbackMethod = "getFallbackUserRating", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value= "2000"), // How long until a request is considered "failed"
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"), // Request pool size for statistic
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"), // How many need to fail before circuit breaks
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") // Time between sleep & retry request
	})
	public UserRating getUserRating(String userId) {
		
		return restTemplate.getForObject("http://RATINGS-DATA-SERVICE/ratingsdata/users/" + userId, UserRating.class);
	}
	
	public UserRating getFallbackUserRating(@PathVariable("userId") String userId) {
		UserRating userRating = new UserRating();
		userRating.setUserId(userId);
		userRating.setUserRating(Arrays.asList(new Rating("0", 0)));
		return userRating;
	}

}
