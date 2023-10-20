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
import frc.robot.commands.MoveLEDCommand;
import frc.robot.commands.SetLEDColorCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.LEDSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final LEDSubsystem m_ledSubsystem = new LEDSubsystem();

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
     m_joystick.button(1).whileTrue(new SetLEDColorCommand(m_ledSubsystem, Color.kRed, true));
     m_joystick.button(2).whileTrue(new SetLEDColorCommand(m_ledSubsystem, Color.kBlue, false));
     m_joystick.button(3).whileTrue(new SetLEDColorCommand(m_ledSubsystem, Color.kGreen, true));
     m_joystick.button(4).whileTrue(new SetLEDColorCommand(m_ledSubsystem, Color.kYellow, false));
     m_joystick.button(5).whileTrue(new SetLEDColorCommand(m_ledSubsystem, Color.kPurple, true));
     m_joystick.button(6).whileTrue(new SetLEDColorCommand(m_ledSubsystem, Color.kBlack, false));
     m_joystick.button(8).whileTrue(new SetLEDColorCommand(m_ledSubsystem, Color.kBlack, true));
     m_joystick.button(7).whileTrue(new MoveLEDCommand(m_ledSubsystem, Color.kRed));
     //blue
     //green
     //yellow
     //purple
     //black

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
