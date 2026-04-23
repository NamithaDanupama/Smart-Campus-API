package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/rooms")
public class SensorRoom {

    // GET /api/v1/rooms — returns all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = DataStore.roomList;
        return Response.ok(rooms).build();
    }

    // GET /api/v1/rooms/{roomId} — returns one specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        for (Room room : DataStore.roomList) {
            if (room.getId().equals(roomId)) {
                return Response.ok(room).build();
            }
        }
        // If we get here, no room was found with that ID
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"The specified room does not exist in the database.\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // POST /api/v1/rooms — creates a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        // Check if a room with this ID already exists
        for (Room currentRoom : DataStore.roomList) {
            if (currentRoom.getId().equals(room.getId())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"A room mapping to this identifier is already registered.\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }
        DataStore.roomList.add(room);
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // DELETE /api/v1/rooms/{roomId} — deletes a room
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room roomToRemove = null;

        // Find the room
        for (Room room : DataStore.roomList) {
            if (room.getId().equals(roomId)) {
                roomToRemove = room;
                break;
            }
        }

        // Room doesn't exist
        if (roomToRemove == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"The specified room does not exist in the database.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Block deletion if room still has sensors
        if (!roomToRemove.getSensorIds().isEmpty()) {
            throw new com.smartcampus.exception.RoomNotEmptyException(
                    "Decommissioning failed: Room '" + roomId + "' contains active hardware."
            );
        }

        DataStore.roomList.remove(roomToRemove);
        return Response.ok("{\"message\": \"Room removed from the system.\"}").build();
    }
}