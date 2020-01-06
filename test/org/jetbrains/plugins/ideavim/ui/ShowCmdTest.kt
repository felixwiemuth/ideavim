package org.jetbrains.plugins.ideavim.ui

import com.maddyhome.idea.vim.helper.StringHelper.parseKeys
import com.maddyhome.idea.vim.option.OptionsManager
import com.maddyhome.idea.vim.ui.ShowCmd
import org.jetbrains.plugins.ideavim.VimTestCase

class ShowCmdTest: VimTestCase() {
  override fun setUp() {
    super.setUp()
    val before = "${c}I found it in a legendary land"
    configureByText(before)
  }

  fun `test showcmd on by default`() {
    OptionsManager.resetAllOptions()
    assertTrue("showcmd", OptionsManager.showcmd.isSet)
  }

  fun `test showcmd shows nothing if disabled`() {
    OptionsManager.showcmd.reset()

    typeText(parseKeys("3"))
    assertEquals("", getShowCmdText())
  }

  fun `test showcmd count`() {
    typeText(parseKeys("3"))
    assertEquals("3", getShowCmdText())
  }

  fun `test showcmd multiple count`() {
    typeText(parseKeys("320"))
    assertEquals("320", getShowCmdText())
  }

  fun `test showcmd incomplete command`() {
    typeText(parseKeys("3d2"))
    assertEquals("3d2", getShowCmdText())
  }

  fun `test showcmd clears on completed command`() {
    typeText(parseKeys("3d2w"))
    assertEquals("", getShowCmdText())
  }

  fun `test showcmd clears on Escape`() {
    typeText(parseKeys("3d2<Esc>"))
    assertEquals("", getShowCmdText())
  }

  fun `test showcmd expands mapped keys`() {
    enterCommand("nmap rrrr d")
    typeText(parseKeys("32rrrr"))
    assertEquals("32d", getShowCmdText())
  }

  // TODO: This test fails because IdeaVim's mapping handler doesn't correctly expand unhandled keys on timeout
//  fun `test showcmd expands ambiguous mapped keys on timeout`() {
    // `rrr` should timeout and replay `rr` which is mapped to `42`
//    enterCommand("nmap rr 42")
//    enterCommand("nmap rrr 55")
//    typeText(parseKeys("12rr"))
//    waitAndAssert { "1242" == getShowCmdText() }
//  }

  fun `test showcmd updates count when expanding mapped keys`() {
    enterCommand("nmap rrrr 55d")
    typeText(parseKeys("32rrrr"))
    assertEquals("3255d", getShowCmdText())
  }

  fun `test showcmd removes count on Delete`() {
    typeText(parseKeys("32<Del>"))
    assertEquals("3", getShowCmdText())
  }

  fun `test showcmd clears if Delete all count chars`() {
    typeText(parseKeys("32<Del><Del>"))
    assertEquals("", getShowCmdText())
  }

  fun `test showcmd removes motion count on Delete`() {
    typeText(parseKeys("32d44<Del><Del>"))
    assertEquals("32d", getShowCmdText())
  }

  fun `test showcmd clears if Delete on operator`() {
    typeText(parseKeys("32d<Del>"))
    assertEquals("", getShowCmdText())
  }

  fun `test showcmd shows nothing in insert mode`() {
    typeText(parseKeys("i", "hello world"))
    assertEquals("", getShowCmdText())
  }

  // TODO: StartInsertDigraphAction is executed before the argument is parsed, so text is immediately cleared
//  fun `test showcmd shows digraph entry in insert mode`() {
//    typeText(parseKeys("i", "<C-K>O"))
//    assertEquals("^KO", getShowCmdText())
//  }

//  fun `test showcmd shows literal entry in insert mode`() {
//    typeText(parseKeys("i", "<C-V>12"))
//    assertEquals("^V12", getShowCmdText())
//  }

//  fun `test showcmd shows literal entry with CTRL-Q in insert mode`() {
//    typeText(parseKeys("i", "<C-Q>12"))
//    assertEquals("^V12", getShowCmdText())  // Yes, Vim shows ^V
//  }

  fun `test showcmd shows register entry in insert mode`() {
    typeText(parseKeys("i", "<C-R>"))
    assertEquals("^R", getShowCmdText())  // Yes, Vim shows ^V
  }

  // Note that Vim shows the number of lines, or rows x cols for visual mode. We don't because IntelliJ already
  // shows this kind of information in the position panel
  fun `test showcmd works in visual mode`() {
    typeText(parseKeys("v", "32f"))
    assertEquals("32f", getShowCmdText())
  }

  fun `test showcmd works in single command mode`() {
    typeText(parseKeys("i", "<C-O>", "32f"))
    assertEquals("32f", getShowCmdText())
  }

  fun `test showcmd only shows last 10 characters of buffer`() {
    typeText(parseKeys("12345678900987654321"))
    assertEquals("0987654321", getShowCmdText())
  }

  fun `test showcmd tooltip shows full buffer`() {
    typeText(parseKeys("12345678900987654321"))
    assertEquals("12345678900987654321", getShowCmdTooltipText())
  }

  // TODO: Select register is currently a standalone action, rather than part of the current command
//  fun `test showcmd shows select register command`() {
//    typeText(parseKeys("\"a32d"))
//    assertEquals("\"a32d", getShowCmdText())
//  }

  private fun getShowCmdText() = ShowCmd.getWidgetText(myFixture.editor!!)
  private fun getShowCmdTooltipText() = ShowCmd.getFullText(myFixture.editor!!)
}