package com.liam.moviecatalogservice.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.liam.moviecatalogservice.models.Rating;
import com.liam.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;


@Service
public class UserRatingInfo {
	
	@Autowired
	private RestTemplate restTemplate;
	
//	// Hysterix Bulkhead - Requests outside of this go to the fallback
//	@HystrixCommand(fallbackMethod = "getFallbackUserRating",
//			threadPoolKey = "userRatingPool", // threadPoolKey name for new thread pool, reusable so that multiple methods can share the same thread pool - Create one for other method as well?
//			threadPoolProperties = {
//				@HystrixProperty(name = "coreSize", value = "20"), // Allowed threads at one time in pool
//				@HystrixProperty(name = "maxQueueSize", value = "10") // Size of waiting line for thread pool
//	})
	
//	// Hysterix Propertes
//	@HystrixCommand(fallbackMethod = "getFallbackUserRating", commandProperties = {
//			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value= "2000"), // How long until a request is considered "failed"
//			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"), // Request pool size for statistic
//			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"), // How many (percentage) need to fail before circuit breaks
//			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") // Time between sleep & retry request
//	})
	
	// Extracted method
	@HystrixCommand(fallbackMethod = "getFallbackUserRating")
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
