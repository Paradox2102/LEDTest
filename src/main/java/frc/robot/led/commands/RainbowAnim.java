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

import org.w3c.dom.css.RGBColor;

import edu.wpi.first.wpilibj.Timer;

public class RainbowAnim extends Command {
  private final LEDSubsystem m_subsystem;


  private int index = 0;
  private final Timer m_timer = new Timer();

  /** Creates a new SpeakerAnim. */
  public RainbowAnim(LEDSubsystem subsystem) {
    Logger.log("RainbowAnim", 3, "RainbowAnim()");
    m_subsystem = subsystem;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Logger.log("SpeakerAnim", 2, "initialize()");
   while (index<20) {
    m_subsystem.setLED(index,Color.fromHSV(index*7, 255, 255));
    index+=1;
   }
   m_subsystem.commit();
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }
  /*
   *
   */

  

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    Logger.log("SpeakerAnim", 2, String.format("end(%b)", interrupted));
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}

// index = math.mod(index+1)
