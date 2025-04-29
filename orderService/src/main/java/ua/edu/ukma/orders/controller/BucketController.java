package ua.edu.ukma.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.orders.dto.TicketDto;
import ua.edu.ukma.orders.dto.TicketsDto;
import ua.edu.ukma.orders.dto.response.BucketResponse;
import ua.edu.ukma.orders.entity.Order;
import ua.edu.ukma.orders.service.BucketService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets/{userId}")
@Tag(name = "Buckets", description = "Management operations for buckets")
@PreAuthorize("#userId == principal")
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
            summary = "Add tickets to bucket",
            description = "Add new tickets to the bucket",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TicketsDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tickets added"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void addTickets(@PathVariable UUID userId, @RequestBody TicketsDto request) {
        bucketService.addTickets(userId, request);
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
                    @ApiResponse(responseCode = "204", description = "Ticket removed"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public void removeTicket(@PathVariable UUID userId, @RequestBody TicketDto request) throws Exception {
        bucketService.removeTicket(userId, request);
    }

    @Operation(
            summary = "Buy tickets from the bucket",
            description = "Buy tickets from the bucket",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order",
                            content = @Content(schema = @Schema(implementation = Order.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/orders")
    public Order buyTickets(@PathVariable UUID userId) throws Exception {
        return bucketService.buyTickets(userId);
    }

    @Operation(
            summary = "Get user's orders",
            description = "Get user's orders",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/orders")
    public List<Order> getUserOrders(@PathVariable UUID userId) throws Exception {
        return bucketService.getOrders(userId);
    }
}
