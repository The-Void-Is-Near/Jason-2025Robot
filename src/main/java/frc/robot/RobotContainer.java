package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.TeleopElevator;
import frc.robot.commands.TeleopOuttake;
import frc.robot.commands.TeleopSwerve;
import frc.robot.commands.zeroing.ManualZeroElevator;
import frc.robot.commands.zeroing.ZeroElevator;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Swerve;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
        /* Subsystems */
        private final Swerve s_Swerve = new Swerve();
        public final Elevator elevator = new Elevator();
        public final Intake intake = new Intake();
        public final Limelight limelight = new Limelight();

        /* PathPlanner */
        private final SendableChooser<Command> autoChooser;

        /* Controllers */
        private final Joystick driver = new Joystick(0);

        /* Drive Controls */
        private final int translationAxis = XboxController.Axis.kLeftY.value;
        private final int strafeAxis = XboxController.Axis.kLeftX.value;
        private final int rotationAxis = XboxController.Axis.kRightX.value;

        /* Driver Buttons */
        private final JoystickButton zeroGyro = new JoystickButton(driver, XboxController.Button.kY.value);
        private final JoystickButton robotCentric = new JoystickButton(driver, XboxController.Button.kStart.value);
        // private final JoystickButton alignLButton = new JoystickButton(driver,
        // XboxController.Button.kLeftBumper.value); // Fix to Left Num
        // private final JoystickButton alignRButton = new JoystickButton(driver,
        // XboxController.Button.kRightBumper.value); // Fix to Right Num
        private final JoystickButton extendElevator = new JoystickButton(driver,
                        XboxController.Button.kRightBumper.value);
        private final JoystickButton retractElevator = new JoystickButton(driver,
                        XboxController.Button.kLeftBumper.value);
        private final JoystickButton outtake = new JoystickButton(driver,
                        XboxController.Button.kA.value);
        private final JoystickButton zeroSubsystem = new JoystickButton(driver, XboxController.Button.kStart.value);

        Command manualZeroSubsystems = new ManualZeroElevator(elevator)
                        .ignoringDisable(true).withName("ManualZeroSubsystems");

        /**
         * The container for the robot. Contains subsystems, OI devices, and commands.
         */
        public RobotContainer() {
                s_Swerve.setDefaultCommand(
                                new TeleopSwerve(
                                                s_Swerve,
                                                () -> -driver.getRawAxis(translationAxis),
                                                () -> -driver.getRawAxis(strafeAxis),
                                                () -> -driver.getRawAxis(rotationAxis),
                                                () -> robotCentric.getAsBoolean()));

                autoChooser = AutoBuilder.buildAutoChooser();
                SmartDashboard.putData("Auto Chooser", autoChooser);
                // Configure the button bindings
                configureButtonBindings();

        }

        /**
         * Use this method to define your button->command mappings. Buttons can be
         * created by
         * instantiating a {@link GenericHID} or one of its subclasses ({@link
         * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing
         * it to a {@link
         * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
         */

        private void configureButtonBindings() {
                /* Driver Buttons */
                zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroHeading()));
                // alignRButton.whileTrue(new TeleopLimelightDrive(s_Swerve, limelight, true));
                // alignLButton.whileTrue(new TeleopLimelightDrive(s_Swerve, limelight, false));
                extendElevator.onTrue(new TeleopElevator(elevator, false)
                                .withInterruptBehavior(InterruptionBehavior.kCancelSelf));
                retractElevator.onTrue(new TeleopElevator(elevator, true)
                                .withInterruptBehavior(InterruptionBehavior.kCancelSelf));
                zeroSubsystem.onTrue(new ZeroElevator(elevator)
                                .withTimeout(Constants.constElevator.ZEROING_TIMEOUT.in(Units.Seconds)));
                outtake.onTrue(new TeleopOuttake(intake));
        }

        /**
         * Use this to pass the autonomous command to the main {@link Robot} class.
         *
         * @return the command to run in autonomous
         */
        public Command getAutonomousCommand() {
                return autoChooser.getSelected();
                // return null;
        }
}
