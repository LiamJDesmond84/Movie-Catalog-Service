package com.liam.moviecatalogservice.controllers;

import java.util.Arrays;

//import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;

import com.liam.moviecatalogservice.models.CatalogItem;
import com.liam.moviecatalogservice.models.Movie;
import com.liam.moviecatalogservice.models.Rating;
//import com.liam.moviecatalogservice.models.Rating;
import com.liam.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;
	

	
	// get all rated movie IDs
	@GetMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
		
		
		UserRating ratings = getUserRating(userId); // Extracted to method
		
		// for each movie ID, call movie info service and get details
		return ratings.getUserRating().stream().map(rating -> getCatalogItem(rating)) // Extracted to method
			.collect(Collectors.toList());
		
	}
	
	// Extracted method
	@HystrixCommand(fallbackMethod = "getFallbackUserRating")
	private UserRating getUserRating(String userId) {
		
		return restTemplate.getForObject("http://RATINGS-DATA-SERVICE/ratingsdata/users/" + userId, UserRating.class);
	}
	

	// Extracted method
	@HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
	private CatalogItem getCatalogItem(Rating rating) {
		Movie movie = restTemplate.getForObject("http://MOVIE-INFO-SERVICE/movies/" + rating.getMovieId(), Movie.class);

		// put them all together (Rating + Movie) to get Catalog Item
		return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
	}
	
	
	
	
	
	// FALLBACK METHODS
	
	private UserRating getFallbackUserRating(@PathVariable("userId") String userId) {
		UserRating userRating = new UserRating();
		userRating.setUserId(userId);
		userRating.setUserRating(Arrays.asList(new Rating("0", 0)));
		return userRating;
	}
	
	
	private CatalogItem getFallbackCatalogItem(Rating rating) {
		return new CatalogItem("Movie name not found", "", rating.getRating());
	}

	

}

//@Autowired
//private WebClient.Builder webClientBuilder;

//Movie movie = webClientBuilder.build()// Gives a WebClient
//.get()
//.uri("http://localhost:8082/movies/" + rating.getMovieId())
//.retrieve().bodyToMono(Movie.class)
//.block();
