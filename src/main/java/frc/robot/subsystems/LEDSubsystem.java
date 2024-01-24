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
  static private AddressableLED m_led = null; // new AddressableLED(0);
  static private AddressableLEDBuffer m_ledBuffer = null; // new AddressableLEDBuffer(2 * Constants.k_LEDLength);
  private int m_startIdx;
  private int m_length;
  private int m_currentMaxLength = 0;

  /** Creates a new LEDSubsystem. */
  public LEDSubsystem(int startIdx, int length) {
    if (m_led == null) {
      m_led = new AddressableLED(0);
    }
    m_startIdx = startIdx;
    m_length = length;
    if (m_startIdx + m_length > m_currentMaxLength) {
      m_currentMaxLength = m_startIdx + m_length;
      m_ledBuffer = new AddressableLEDBuffer(m_currentMaxLength);
      m_led.setLength(m_currentMaxLength);
    }
  }

  public void setLedData(AddressableLEDBuffer srcBuffer, int startIdx) {
    for (int i = 0; i < m_length; ++i) {
      Color color = srcBuffer.getLED(i);
      m_ledBuffer.setLED(i + startIdx, color);
    }

  }

  public void setData(AddressableLEDBuffer buffer) {
    setLedData(buffer, m_startIdx);

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
