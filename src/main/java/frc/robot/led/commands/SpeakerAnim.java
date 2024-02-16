// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.led.commands;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.apriltagsCamera.Logger;
import frc.robot.led.subsystems.LEDSubsystem;

import org.w3c.dom.css.RGBColor;

import edu.wpi.first.wpilibj.Timer;

public class SpeakerAnim extends Command {
  private final LEDSubsystem m_subsystem;
  private final Color m_color;

  private float delay=0.1f;
  private int index =0;
  private final Timer m_timer = new Timer();

  /** Creates a new SpeakerAnim. */
  public SpeakerAnim(LEDSubsystem subsystem, Color color) {
    Logger.log("SpeakerAnim", 3, "SpeakerAnim()");
    m_subsystem = subsystem;
    m_color = color;
    
   
    

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    Logger.log("SpeakerAnim", 2, "initialize()");
    m_timer.start();
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
if (m_timer.get()>=delay){
m_timer.reset();
index+=1;
}
if(index>20){
  index=1;
}
    m_subsystem.setAllLEDs(Color.fromHSV(0, 255, 100));
    m_subsystem.setLED(index, m_color);
    m_subsystem.setLED(index+10, m_color);
    m_subsystem.setLED(index-10, m_color);
    m_subsystem.setLED(index+15, m_color);
    m_subsystem.setLED(index-15, m_color);
     m_subsystem.setLED(index+20, m_color);
    m_subsystem.setLED(index-20, m_color);
    m_subsystem.setLED(index+5, m_color);
    m_subsystem.setLED(index-5, m_color);
   m_subsystem.commit();
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
