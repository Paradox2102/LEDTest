// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.led.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDSubsystem extends SubsystemBase {
  static private AddressableLED m_led = null; // new AddressableLED(0);
  static private AddressableLEDBuffer m_ledBuffer = null; // new AddressableLEDBuffer(2 * Constants.k_LEDLength);
  private int m_startIdx;
  private int m_length;
  private int m_currentMaxLength = 0;
  private boolean m_commit = false;

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

    m_led.start();
  }

  /*
   * Sets the color of a single LED on the string
   */
  public void setLED(int idx, Color color) {
    if ((idx >= 0) && (idx < m_length)) {
      m_ledBuffer.setLED(idx + m_startIdx, color);
    }
  }

  /*
   * Sets the color of multiple LEDs on the string
   */
  public void setLEDs(int start, int length, Color color) {
    if (start < 0) {
      start = 0;
    }

    if ((start + length) > m_length) {
      length = m_length - start;
    }

    for (int i = start ; i < length ; i++) {
      m_ledBuffer.setLED(i + m_startIdx, color);
    }
  }

  /*
   * Sets the color for all the LEDs on the string
   */
  public void setAllLEDs(Color color) {
    for (int i = 0 ; i < m_length ; i++) {
      m_ledBuffer.setLED(i + m_startIdx, color);
    }
  }

  /*
   * Commit the current changes to the string
   */
  public void commit() {
    m_commit = true;
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
    if (m_commit) {
      m_led.setData(m_ledBuffer);
      m_commit = false;
    }
  }
}
