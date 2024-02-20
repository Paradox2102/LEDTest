// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.led.commands;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.apriltagsCamera.Logger;
import frc.robot.led.subsystems.LEDSubsystem;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.RGBColor;
import java.util.List;
import edu.wpi.first.wpilibj.Timer;

public class ProgressBar extends Command {
  private final LEDSubsystem m_subsystem;
  private int index = 0;
  private int remaining = 20;
  private final Timer m_timer = new Timer();

  /** Creates a new SpeakerAnim. */
  public ProgressBar(LEDSubsystem subsystem) {
    Logger.log("ProgressBar", 3, "ProgressBar()");
    m_subsystem = subsystem;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Logger.log("ProgressBar", 2, "initialize()");
    m_timer.start();
     index = 0;
   remaining = 20;
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (m_timer.get() > 0.5) {
      m_timer.reset();
      remaining -= 1;
      m_subsystem.setAllLEDs(Color.kBlack);
      while (index < remaining) {
        m_subsystem.setLED(index, Color.fromHSV(remaining*3, 255, 255));
        index += 1;
      }
      index=0;
      m_subsystem.commit();
    }

  }
  /*
   *
   */

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    Logger.log("ProgressBar", 2, String.format("end(%b)", interrupted));
  
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return remaining < 0;
  }
}

// index = math.mod(index+1)
