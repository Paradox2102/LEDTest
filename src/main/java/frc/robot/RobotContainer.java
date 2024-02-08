// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.Autos;
// import frc.robot.led.commands.DrawHorzLineCommand;
import frc.robot.led.commands.MoveLEDCommand;
import frc.robot.led.commands.SetLEDColorCommand;
import frc.robot.led.subsystems.LEDSubsystem;
import frc.robot.subsystems.ExampleSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final LEDSubsystem m_leftLedSubsystem = new LEDSubsystem(0, Constants.k_LEDLength);
  private final LEDSubsystem m_rightLedSubsystem = new LEDSubsystem(Constants.k_LEDLength, Constants.k_LEDLength);
  // private final LEDSubsystem m_2DLedSubsystem = new LEDSubsystem(0, 32, 8);

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandJoystick m_joystick = new CommandJoystick(0);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
      m_joystick.button(1).onTrue(new SetLEDColorCommand(m_leftLedSubsystem, Color.kRed));
      m_joystick.button(2).onTrue(new SetLEDColorCommand(m_leftLedSubsystem, Color.kBlack));
      m_joystick.button(3).onTrue(new SetLEDColorCommand(m_rightLedSubsystem, Color.kGreen));
      m_joystick.button(4).onTrue(new SetLEDColorCommand(m_rightLedSubsystem, Color.kBlack));
      m_joystick.button(5).onTrue(new MoveLEDCommand(m_leftLedSubsystem, Color.kRed));
      // m_joystick.button(6).onTrue(new DrawHorzLineCommand(m_2DLedSubsystem, Color.kBlue));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
