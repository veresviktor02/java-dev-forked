package core.service.impl;

import com.epam.training.ticketservice.core.dto.UserDto;
import com.epam.training.ticketservice.core.model.User;
import com.epam.training.ticketservice.core.repository.UserRepository;
import com.epam.training.ticketservice.core.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserServiceImpl underTest = new UserServiceImpl(userRepository);
    User testAdmin = new User("admin", "admin", User.Role.ADMIN);
    User testUser = new User("user", "user", User.Role.USER);

    @Test
    public void testLoginShouldSetLoggedInUserWhenUsernameAndPasswordAreCorrect() {
        //Given
        when(userRepository.findByUsername(testAdmin.getUsername())).thenReturn(Optional.of(testAdmin));

        //When
        Optional<UserDto> actual = underTest.login(testAdmin.getUsername(), testAdmin.getPassword());

        //Then
        assertTrue(actual.isPresent());
        assertEquals(testAdmin.getUsername(), actual.get().username());
        assertEquals(User.Role.ADMIN, actual.get().role());
        verify(userRepository).findByUsername(testAdmin.getUsername());
    }

    @Test
    public void testLoginShouldReturnOptionalEmptyWhenUsernameAndPasswordAreIncorrect(){
        //Given
        Optional<UserDto> excepted = Optional.empty();
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());

        //When
        Optional<UserDto> actual = underTest.login(testUser.getUsername(), testUser.getPassword());

        //Then
        assertEquals(excepted, actual);
        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    void testLoginShouldReturnOptionalUserDtoEmptyWhenRoleIsNotAdmin() {
        //Given
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        //When
        Optional<UserDto> actual = underTest.login(testUser.getUsername(), "resu");

        //Then
        assertTrue(actual.isEmpty());
        verify(userRepository).findByUsername(testUser.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testIsValidCredentialsShouldReturnFalseWhenCredentialsAreInvalid() {
        //Given

        //When
        boolean isValid = underTest.isValidCredentials(testUser, testUser.getPassword());

        //Then
        assertFalse(isValid);
    }

    @Test
    public void testLogoutShouldReturnOptionalEmptyWhenUserIsNotSignedIn(){
        //Given
        Optional<UserDto> userDtoOptionalEmpty = Optional.empty();

        //When
        Optional<UserDto> userDtoOptionalUnderTest = underTest.logout();

        //Then
        assertEquals(userDtoOptionalEmpty, userDtoOptionalUnderTest);
    }






















}
