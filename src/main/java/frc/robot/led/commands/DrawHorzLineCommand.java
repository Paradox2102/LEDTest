// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.led.commands;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.apriltagsCamera.Logger;
import frc.robot.led.subsystems.LEDSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class DrawHorzLineCommand extends InstantCommand {
  private final LEDSubsystem m_subsystem;
  private final Color m_color;

  public DrawHorzLineCommand(LEDSubsystem subsystem, Color color) {
    Logger.log("DrawHorzLineCommand", 3, "DrawHorzLineCommand()");
    
    m_subsystem = subsystem;
    m_color = color;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_subsystem);
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    for (int x = 0 ; x < 32 ; x++)
    {
      m_subsystem.setLED(x, 4, m_color);
    }
    m_subsystem.commit();
  }
}
