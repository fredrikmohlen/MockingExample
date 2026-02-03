package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock
    NotificationService notificationService;
    @Mock
    RoomRepository roomRepository;
    @Mock
    TimeProvider timeProvider;
    @InjectMocks
    BookingSystem bookingSystem;
//Metoder som finns i BookingSystem.
    //boolean bookRoom, 4 if-satser + tomt rum
    //List<Room> getAvailableRooms, 2 if-satser
    //boolean cancelBooking, 3 if-satser


    @Test
    void whenMakingCorrectBookingBookRoomReturnTrue() {

        String roomId = "5";
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = startTime.plusDays(3);

        when(timeProvider.getCurrentTime()).thenReturn(now);

        Room room = new Room(roomId, "Room Five");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);

        assertThat(result).isTrue();

        Mockito.verify(roomRepository).save(room);

    }

    @ParameterizedTest
    @CsvSource({
            ", 2026-02-02T10:00, 2026-02-02T12:00, 'Bokning kräver giltiga start- och sluttider samt rum-id'", // roomId is null
            "5, , 2026-02-02T12:00, 'Bokning kräver giltiga start- och sluttider samt rum-id'",                // startTime is null
            "5, 2026-02-02T10:00, , 'Bokning kräver giltiga start- och sluttider samt rum-id'",                // endTime is null
            "5, 2025-02-02T10:00, 2026-02-02T12:00, 'Kan inte boka tid i dåtid'",                              // startTime is in the past
            "5, 2026-02-02T10:00, 2026-01-02T12:00, 'Sluttid måste vara efter starttid'"                       // endTime is before starTime
    })
    void shouldThrowExceptionForInvalidInputInBookRoom(String roomId, LocalDateTime startTime, LocalDateTime endTime, String expectedMessage) {
        if (roomId != null && startTime != null && endTime != null) {
            LocalDateTime testNow = LocalDateTime.of(2026, 1, 1, 0, 0);
            when(timeProvider.getCurrentTime()).thenReturn(testNow);
        }
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void whenRoomDoesNotExistBookRoomReturnException() {
        String roomId = "5";
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = startTime.plusDays(3);

        when(timeProvider.getCurrentTime()).thenReturn(now);

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rummet existerar inte");
    }

    @Test
    void whenARoomIsOccupiedBookRoomReturnFalse() {

        String roomId = "5";
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = startTime.plusDays(3);

        when(timeProvider.getCurrentTime()).thenReturn(now);

        Room room = new Room(roomId, "Room Five");
        Booking existingBooking = new Booking("existing-id", roomId, startTime, endTime);
        room.addBooking(existingBooking);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);

        assertThat(result).isFalse();

        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldReturnTrueEvenWhenNotificationFails() throws NotificationException {
        String roomId = "5";
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = startTime.plusDays(3);

        when(timeProvider.getCurrentTime()).thenReturn(now);

        Room room = new Room(roomId, "Room Five");
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        doThrow(NotificationException.class)
                .when(notificationService).sendBookingConfirmation(any());

        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);
        assertThat(result).isTrue();

        verify(roomRepository).save(room);
        Mockito.verify(notificationService).sendBookingConfirmation(any());
    }

    @ParameterizedTest
    @CsvSource({
            "2026-02-02T10:00, ,'Måste ange både start- och sluttid'",
            " , 2026-02-02T10:00,'Måste ange både start- och sluttid'",
            "2026-02-02T10:00,2026-01-05T10:00,'Sluttid måste vara efter starttid'"
    })
    void shouldThrowExceptionForInvalidInputInGetAvailableRooms(LocalDateTime startTime, LocalDateTime endTime, String expectedMessage) {

        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void whenGivenOccupiedAndAvailableRoomGetAvailableRoomsShouldReturnAvailableRooms() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = startTime.plusDays(3);

        Room roomA = new Room("5", "Room Five");
        Room roomB = new Room("6", "Room Six");
        Room roomC = new Room("7", "Room Seven");
        Booking booking = new Booking("b1","6", startTime, endTime);
        roomB.addBooking(booking);

        when(roomRepository.findAll()).thenReturn(List.of(roomA, roomB, roomC));

        List<Room> result = bookingSystem.getAvailableRooms(startTime, endTime);

        assertThat(result)
                .hasSize(2)
                .containsExactly(roomA, roomC)
                .doesNotContain(roomB);
            }
            // cancel booking
            // make sure that roomWithBooking returns false if .isEmpty()
    // om endTime innan StartTime
    // Lyckad cancelering

    @Test
    void whenNullBookingIdCancelBookingShouldReturnException(){

        String bookingId = null;

        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Boknings-id kan inte vara null");
    }

    @Test
    void whenCancelingAnExistingBookingCancelBookingShouldReturnTrue() throws NotificationException {
        String roomId = "5";
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime startTime = now.plusDays(1);
        LocalDateTime endTime = startTime.plusDays(3);

        when(timeProvider.getCurrentTime()).thenReturn(now);
        Room room = new Room(roomId, "Room Five");
        Room room2 = new Room("6", "Room Six");

        when(roomRepository.findAll()).thenReturn(List.of(room, room2));

        Booking fakeBooking = new Booking("fakeBookingId", roomId, startTime, endTime);
        Booking fakeBooking2 = new Booking("fakeBookingId2", "6", startTime, endTime);
        room.addBooking(fakeBooking);
        room2.addBooking(fakeBooking2);

        assertThat(bookingSystem.cancelBooking(fakeBooking.getId())).isTrue();
        assertThat(room.hasBooking("fakeBookingId")).isFalse();

        verify(roomRepository).save(room);
        verify(notificationService).sendCancellationConfirmation(fakeBooking);

    }

    @Test
    void cancelBookingShouldReturnFalseWhenBookingNotFound(){

        Room room = new Room("5", "Room Five");
        Room room2 = new Room("6", "Room Six");

        String bookingId = "fakeBookingId";

        when(roomRepository.findAll()).thenReturn(List.of(room, room2));

        boolean result = bookingSystem.cancelBooking(bookingId);

        assertThat(result).isFalse();

        verify(roomRepository, never()).save(any());
    }

    @Test
    void cancelBookingShouldThrowExceptionWhenBookinghasAlreadyStarted() {
        String bookingId = "past-booking";
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime startTime = now.minusHours(1);
        LocalDateTime endTime = startTime.plusHours(3);

        when(timeProvider.getCurrentTime()).thenReturn(now);

        Room room = new Room("5", "Room Five");
        Booking pastBooking = new Booking(bookingId,"5", startTime, endTime);
        room.addBooking(pastBooking);

        when(roomRepository.findAll()).thenReturn(List.of(room));

        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Kan inte avboka påbörjad eller avslutad bokning");

        assertThat(room.hasBooking(bookingId)).isTrue();

    }
}
