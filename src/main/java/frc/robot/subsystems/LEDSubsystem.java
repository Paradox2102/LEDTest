// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class LEDSubsystem extends SubsystemBase {
  private final AddressableLED m_led = new AddressableLED(0);
  private final AddressableLEDBuffer m_ledBuffer = new AddressableLEDBuffer(2 * Constants.k_LEDLength);

  /** Creates a new LEDSubsystem. */
  public LEDSubsystem() {
    m_led.setLength(Constants.k_LEDLength);
  }

  public void setData(AddressableLEDBuffer buffer, Boolean leftSide) {
    if (leftSide){
      int i = 0;
      Color color;
      while(i<Constants.k_LEDLength){
        color = buffer.getLED(i);
        m_ledBuffer.setLED(i, color);
        i++;
      }
    } else {
      int i = Constants.k_LEDLength;
      Color color;
      while(i<2 * Constants.k_LEDLength){
        color = buffer.getLED(i - Constants.k_LEDLength);
        m_ledBuffer.setLED(i, color);
        i++;
      }
    }
    m_led.setData(m_ledBuffer);
  }

  public void start() {
    m_led.start();

  }

  public void end() {
    m_led.stop();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
