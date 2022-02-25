package com.liam.moviecatalogservice.controllers;

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
//import com.liam.moviecatalogservice.models.Rating;
import com.liam.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;
	

	
	// get all rated movie IDs
	@GetMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
		
		
		UserRating ratings = restTemplate.getForObject("http://RATINGS-DATA-SERVICE/ratingsdata/users/" + userId, UserRating.class);
		
		// for each movie ID, call movie info service and get details
		return ratings.getUserRating().stream().map(rating -> {
			Movie movie = restTemplate.getForObject("http://MOVIE-INFO-SERVICE/movies/" + rating.getMovieId(), Movie.class);
			

			// put them all together
			return new CatalogItem(movie.getName(), "Test", rating.getRating());
			})
			.collect(Collectors.toList());
				
		
		
	}

}

//@Autowired
//private WebClient.Builder webClientBuilder;

//Movie movie = webClientBuilder.build()// Gives a WebClient
//.get()
//.uri("http://localhost:8082/movies/" + rating.getMovieId())
//.retrieve().bodyToMono(Movie.class)
//.block();
