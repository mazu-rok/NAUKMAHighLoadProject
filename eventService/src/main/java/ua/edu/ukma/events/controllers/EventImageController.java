package ua.edu.ukma.events.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ua.edu.ukma.events.dto.responses.EventResponse;
import ua.edu.ukma.events.services.EventService;

@RestController
@RequestMapping("/api/v1/events/{eventId}/images")
@Tag(name = "Events Images", description = "CRUD operations for events images")
public class EventImageController {

    private final EventService eventService;

    public EventImageController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
      summary = "Get event image by ID",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "eventId", required = true, description = "UUID of the event"),
        @Parameter(in = ParameterIn.PATH, name = "imageId", required = true, description = "UUID of the event image")
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "The image"),
        @ApiResponse(responseCode = "404", description = "Event not found")
      }
    )
    @PreAuthorize(
     "@eventAuth.hasPublicStatus(#eventId) or (hasRole('ADMIN') and @eventAuth.hasDraftStatus(#eventId))"
    )
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable UUID eventId, @PathVariable UUID imageId) throws IOException {
        Optional<InputStream> optionalImage = eventService.getImage(eventId, imageId);
        if (optionalImage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event or image not found");
        }
        return IOUtils.toByteArray(optionalImage.get());
    }

    @Operation(
      summary = "Saves image for event",
      description = "Saves image for the event",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "eventId", required = true,
                   description = "UUID of the event for which to save the image"),
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "Image saved",
                     content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(responseCode = "404", description = "Event not found")
      }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EventResponse uploadImage(
        @PathVariable UUID eventId,
        @RequestParam(required = true) MultipartFile file
    ) throws IOException {
        return eventService.addImage(eventId, file).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")
        );
    }

    @Operation(
      summary = "Delete an image from event",
      description = "Deletes the image identified by `eventId` and `imageId`.",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "eventId", required = true,
                   description = "UUID of the event for which to delete the image"),
        @Parameter(in = ParameterIn.PATH, name = "imageId", required = true,
                   description = "UUID of the image to delete")
      },
      responses = {
        @ApiResponse(responseCode = "204", description = "Image deleted"),
        @ApiResponse(responseCode = "404", description = "Event not found")
      }
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public EventResponse deleteImage(
        @PathVariable UUID eventId,
        @PathVariable UUID imageId
    ) {
        return eventService.removeImage(eventId, imageId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")
        );
    }
}
