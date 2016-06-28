package org.crysil.instance.gridh.desktop;

import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.skin.TextAreaSkin;

public class FixedTextAreaSkin extends TextAreaSkin{

  public FixedTextAreaSkin() {
  }
  
  @Override
  public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
      //boolean consumed = super.mouseClick(component, button, x, y, count);

      TextArea textArea = (TextArea) component;

      if (button == Mouse.Button.LEFT) {
          int index = getInsertionPoint(x, y);
          if (index != -1) {
              if (count == 2) {
                  selectSpan(textArea, index);
              } else if (count == 3) {
                  textArea.setSelection(textArea.getRowOffset(index),
                      textArea.getRowLength(index));
              }
          }
      }
      return false;
  }

  private void selectSpan(TextArea textArea, int start) {
    int rowStart = textArea.getRowOffset(start);
    int rowLength = textArea.getRowLength(start);
    if (start - rowStart >= rowLength) {
        start = rowStart + rowLength - 1;
        if (start < 0) {
            return;
        }
        char ch = textArea.getCharacterAt(start);
        if (ch == '\r' || ch == '\n') {
            start--;
        }
    }
    if (start < 0) {
        return;
    }
    char ch = textArea.getCharacterAt(start);
    int selectionStart = start;
    int selectionLength = 1;
    if (Character.isWhitespace(ch)) {
        // Move backward to beginning of whitespace block
        // but not before the beginning of the line.
        do {
            selectionStart--;
        } while (selectionStart >= rowStart
            && Character.isWhitespace(textArea.getCharacterAt(selectionStart)));
        selectionStart++;
        selectionLength = start - selectionStart;
        // Move forward to end of whitespace block
        // but not past the end of the text or the end of line
        do {
            selectionLength++;
        } while (selectionStart + selectionLength - rowStart < rowLength
            && Character.isWhitespace(textArea.getCharacterAt(selectionStart + selectionLength)));
    } else if (Character.isJavaIdentifierPart(ch)) {
        // Move backward to beginning of identifier block
        do {
            selectionStart--;
        } while (selectionStart >= rowStart
            && Character.isJavaIdentifierPart(textArea.getCharacterAt(selectionStart)));
        selectionStart++;
        selectionLength = start - selectionStart;
        // Move forward to end of identifier block
        // but not past end of text
        do {
            selectionLength++;
        } while (selectionStart + selectionLength - rowStart < rowLength
            && Character.isJavaIdentifierPart(textArea.getCharacterAt(selectionStart
                + selectionLength)));
    } else {
        return;
    }
    textArea.setSelection(selectionStart, selectionLength);
}
}
