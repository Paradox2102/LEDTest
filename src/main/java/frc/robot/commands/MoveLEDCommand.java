// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.LEDSubsystem;

public class MoveLEDCommand extends CommandBase {
  private final LEDSubsystem m_subsystem;
  private final AddressableLEDBuffer m_led = new AddressableLEDBuffer(Constants.k_LEDLength);
  private final Color m_color;
  private int m_index;

  /** Creates a new MoveLEDCommand. */
  public MoveLEDCommand(LEDSubsystem subsystem, Color color) {
    m_subsystem = subsystem;
    m_color = color;

    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_subsystem.start();
    m_index = 0;
  }

  public void setLED(Color color) {
    int i = 0;
    while (i < Constants.k_LEDLength) {
      m_led.setLED(i, color);
      i++;
    }
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    setLED(Color.kBlack);
    m_led.setLED(m_index, m_color);
    m_index = (m_index + 1) % Constants.k_LEDLength;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}

// index = math.mod(index+1)
