package ua.edu.ukma.events.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.services.EventService;

@RestController
@RequestMapping("/events/{id}/images")
@Tag(name = "Events Images", description = "CRUD operations for events images")
public class EventImageController {

    private final EventService eventService;

    public EventImageController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
      summary = "Saves image for event",
      description = "Saves image for the event",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", required = true,
                   description = "UUID of the event for which to save the image"),
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "Image saved",
                     content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "404", description = "Event not found")
      }
    )
    @PostMapping
    public EventResponse uploadImage(
        @PathVariable UUID id
    ) throws IOException {
        return eventService.addImage(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")
        );
    }

    @Operation(
      summary = "Delete an image from event",
      description = "Deletes the image identified by `id` and `image_id`.",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", required = true,
                   description = "UUID of the event for which to delete the image"),
        @Parameter(in = ParameterIn.PATH, name = "image_id", required = true,
                   description = "UUID of the image to delete")
      },
      responses = {
        @ApiResponse(responseCode = "204", description = "Image deleted"),
        @ApiResponse(responseCode = "404", description = "Event not found")
      }
    )
    @DeleteMapping("/{imageId}")
    public EventResponse deleteImage(
        @PathVariable UUID id,
        @PathVariable UUID imageId
    ) {
        return eventService.removeImage(id, imageId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")
        );
    }
}
