package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingSystemTest {

@Mock
    NotificationService notificationService;
@Mock
    RoomRepository roomRepository;
@Mock
    TimeProvider timeProvider;
@InjectMocks
    BookingSystem bookingSystem;
//Metoder som finns i BookingSystem.
    //boolean bookRoom, 4 if-satser
    //List<Room> getAvailableRooms, 2 if-satser
    //boolean cancelBooking, 3 if-satser


@Test
    void whenMakingCorrectBookingBookRoomReturnTrue(){

    String roomId = "5";
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startTime = now.plusDays(1);
    LocalDateTime endTime = startTime.plusDays(3);

    when(timeProvider.getCurrentTime()).thenReturn(now);

    Room room = new Room(roomId,"Room Five");

    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

    boolean result = bookingSystem.bookRoom(roomId,startTime,endTime);

    assertThat(result).isTrue();

    Mockito.verify(roomRepository).save(room);

}

}
