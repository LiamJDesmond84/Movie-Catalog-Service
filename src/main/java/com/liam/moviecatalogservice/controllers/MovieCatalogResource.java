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
	@HystrixCommand(fallbackMethod = "getFallbackCatalog")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
		
		
		UserRating ratings = getUserRating(userId);
		
		// for each movie ID, call movie info service and get details
		return ratings.getUserRating().stream().map(rating -> getCatalogItem(rating))
			.collect(Collectors.toList());
		
	}
	
	// Extracted method
	private UserRating getUserRating(String userId) {
		
		return restTemplate.getForObject("http://RATINGS-DATA-SERVICE/ratingsdata/users/" + userId, UserRating.class);
	}
	

	// Extracted method
	private CatalogItem getCatalogItem(Rating rating) {
		Movie movie = restTemplate.getForObject("http://MOVIE-INFO-SERVICE/movies/" + rating.getMovieId(), Movie.class);

		// put them all together
		return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
	}


	
	public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
		return Arrays.asList(new CatalogItem("No movie", "", 0));
	}

}

//@Autowired
//private WebClient.Builder webClientBuilder;

//Movie movie = webClientBuilder.build()// Gives a WebClient
//.get()
//.uri("http://localhost:8082/movies/" + rating.getMovieId())
//.retrieve().bodyToMono(Movie.class)
//.block();
