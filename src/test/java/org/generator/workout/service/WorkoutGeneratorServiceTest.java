package org.generator.workout.service;

import org.generator.workout.model.AppUser;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WorkoutGeneratorServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutProgramRepository programRepository;

    @InjectMocks
    private WorkoutGeneratorService service;

    @BeforeEach
    void setUp() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
    }


}
