package ua.edu.ukma.events.controllers;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;

import ua.edu.ukma.events.dto.responses.PlaceResponse;
import ua.edu.ukma.events.services.PlaceService;

import java.util.UUID;
import java.util.List;


@RestController
@RequestMapping("/api/v1/events/places")
@Tag(name = "Places", description = "CRUD operations for places")
public class PlaceController {

  private final PlaceService placeService;

  public PlaceController(PlaceService placeService) {
    this.placeService = placeService;
  }

  @Operation(
    summary = "Get all places by eventId",
    responses = {
      @ApiResponse(responseCode = "200", description = "List of places by eventId",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceResponse.class))))
    }
  )
  @GetMapping
  public List<PlaceResponse> listPlacesByEventId(@RequestParam UUID eventId) {
    return placeService.listPlacesByEvent(eventId);
  }
}