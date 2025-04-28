package ua.edu.ukma.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.orders.dto.TicketDto;
import ua.edu.ukma.orders.dto.response.BucketResponse;
import ua.edu.ukma.orders.service.BucketService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets/{userId}")
@Tag(name = "Buckets", description = "Management operations for buckets")
public class BucketController {
    private final BucketService bucketService;

    @Operation(
            summary = "Get user's bucket",
            description = "Get user's bucket",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bucket",
                            content = @Content(schema = @Schema(implementation = BucketResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public BucketResponse getBucket(@PathVariable UUID userId) {
        return bucketService.getBucket(userId);
    }

    @Operation(
            summary = "Add ticket to bucket",
            description = "Add new ticket to the bucket",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TicketDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Ticket added"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void addTicket(@PathVariable UUID userId, @RequestBody TicketDto request) {
        bucketService.addTicket(userId, request);
    }

    @Operation(
            summary = "Delete ticket from the bucket",
            description = "Delete ticket from the bucket",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TicketDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Ticket removed"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public void removeTicket(@PathVariable UUID userId, @RequestBody TicketDto request) throws Exception {
        bucketService.removeTicket(userId, request);

    }
}
