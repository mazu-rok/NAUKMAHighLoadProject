package ua.edu.ukma.events.controllers;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import ua.edu.ukma.events.dto.requests.EventRequest;
import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.entities.Event.EventStatus;
import ua.edu.ukma.events.services.EventService;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "CRUD operations for events")
public class EventController {

    private final EventService eventService;
    private final Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
      summary = "Create a new event",
      description = "Creates a new event. The `eventId`, `createdAt`, and `updatedAt` are generated by the system.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
          schema = @Schema(implementation = EventRequest.class)
        )
      ),
      responses = {
        @ApiResponse(responseCode = "201", description = "Event created",
                     content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
      }
    )
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
        @RequestBody EventRequest event) 
    {
        logger.trace("Received event to save: %s".formatted(event.toString()));
        EventResponse created = eventService.create(event);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Operation(
      summary = "Get all events",
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", required = true, description = "Pagination page"),
        @Parameter(in = ParameterIn.QUERY, name = "size", required = true, description = "Number of elements on one page. Max is 50"),
        @Parameter(in = ParameterIn.QUERY, name = "sortBy", required = true, description = "Field by which to sort"),
        @Parameter(in = ParameterIn.QUERY, name = "direction", required = true, description = "desc/asc"),
        @Parameter(in = ParameterIn.QUERY, name = "status", required = false, description = "Filter for event status. If not used, then returns events with all statuses"),
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "List of events",
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))))
      }
    )
    @GetMapping
    public Page<EventResponse> listEvents(
      @Min(value = 0, message = "Size must be >= 0")
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") 
      @Min(value = 1, message = "Size must be >= 1")
      @Max(value = 50, message = "Cannot request more than 50 events at a time")
      int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      @RequestParam(required = false) EventStatus status) {
        return eventService.listAll(page, size, sortBy, direction, Optional.ofNullable(status));
    }

    @Operation(
      summary = "Get event by ID",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", required = true,
                   description = "UUID of the event")
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "The event",
                     content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "404", description = "Event not found")
      }
    )
    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable("id") UUID id) {
        return eventService.getById(id).orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")
        );
    }

    @Operation(
      summary = "Update an existing event",
      description = "Updates the event identified by `id`. Fields not provided in the payload will remain unchanged.",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", required = true,
                   description = "UUID of the event to update")
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(schema = @Schema(implementation = EventRequest.class))
      ),
      responses = {
        @ApiResponse(responseCode = "200", description = "Event updated",
                     content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
      }
    )
    @PutMapping("/{id}")
    public EventResponse updateEvent(
        @PathVariable("id") UUID id,
        @RequestBody EventRequest event
    ) {
        return eventService.update(id, event).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")
        );
    }

    @Operation(
      summary = "Delete an event",
      description = "Deletes the event identified by `id`.",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", required = true,
                   description = "UUID of the event to delete")
      },
      responses = {
        @ApiResponse(responseCode = "204", description = "Event deleted"),
        @ApiResponse(responseCode = "404", description = "Event not found")
      }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable("id") UUID id) {
        eventService.delete(id);
    }
}