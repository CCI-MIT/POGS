package edu.mit.cci.pogs.view.chatentry;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.dao.chatscript.ChatScriptDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.utils.SqlTimestampPropertyEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.List;

@Controller
//@RequestMapping(value = "/admin/chatentry")
public class ChatEntryController {

    @Autowired
    private ChatEntryDao chatEntryDao;

    @Autowired
    private ChatScriptDao chatScriptDao;

    @GetMapping("/chatentries/")
    public String getChatEntry(Model model) {
        List<ChatEntry> chatEntries = chatEntryDao.list();
        if (chatEntries!=null)
            model.addAttribute("chatscript",chatScriptDao.get(chatEntries.get(0).getChatScriptId()));
        else
            model.addAttribute("chatscript", null);
        model.addAttribute("chatentryList", chatEntries);
        return "chatentry/chatentry-list";
    }

    @GetMapping("/admin/chatscripts/{chatscriptId}/chatentries/{id}")
    public String getChatEntries(@PathVariable("chatscriptId") Long chatscriptId, @PathVariable("id") Long id, Model model) {
        ChatScript chatScript = chatScriptDao.get(chatscriptId);
        model.addAttribute("chatscript",chatScript);
        model.addAttribute("chatentry", chatEntryDao.get(id));
        return "chatentry/chatentry-display";
    }

    @GetMapping("/admin/chatscripts/{chatscriptId}/chatentries/create")
    public String createChatEntry(@PathVariable("chatscriptId") Long chatscriptId, Model model) {

        ChatEntry chatEntry = new ChatEntry();
        //Set relationship
        ChatScript chatScript = chatScriptDao.get(chatscriptId);
        chatEntry.setChatScriptId(chatScript.getId());

        model.addAttribute("chatscript", chatScript);
        model.addAttribute("chatentry", chatEntry);
        return "chatentry/chatentry-edit";
    }

    @GetMapping("/admin/chatscripts/{chatscriptId}/chatentries/{id}/edit")
    public String editChatEntry(@PathVariable("chatscriptId") Long chatscriptId, @PathVariable("id") Long id, Model model) {

        ChatEntry chatEntry = new ChatEntry(chatEntryDao.get(id));
        //Set relationship
        ChatScript chatScript = chatScriptDao.get(chatscriptId);
        chatEntry.setChatScriptId(chatScript.getId());
        model.addAttribute("chatscript", chatScript);
        model.addAttribute("chatentry", chatEntry);
        return "chatentry/chatentry-edit";
    }

    @PostMapping("/admin/chatentries")
    public String saveChatEntry(@ModelAttribute ChatEntry chatentry, RedirectAttributes redirectAttributes) {

        if (chatentry.getId() == null) {
            chatEntryDao.create(chatentry);
            MessageUtils.addSuccessMessage("Chat Entry created successfully!", redirectAttributes);
        } else {
            chatEntryDao.update(chatentry);
            MessageUtils.addSuccessMessage("Chat Entry updated successfully!", redirectAttributes);
        }
        return "redirect:/admin/chatscripts/" + chatentry.getChatScriptId();
    }

}
