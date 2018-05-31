package edu.mit.cci.pogs.view.chatscript;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.dao.chatscript.ChatScriptDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;
import edu.mit.cci.pogs.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/chatscripts")
public class ChatScriptController {

    @Autowired
    private ChatScriptDao chatScriptDao;

    @Autowired
    private ChatEntryDao chatEntryDao;

    @GetMapping
    public String getChatScript(Model model) {
         model.addAttribute("chatscriptList", chatScriptDao.list());
        return "chatscript/chatscript-list";
    }

    @GetMapping("{chatscriptId}")
    public String getStudies(@PathVariable("chatscriptId") Long chatscriptId, Model model) {
        ChatScript chatScript = chatScriptDao.get(chatscriptId);
        model.addAttribute("chatscript", chatScript);
        List<ChatEntry> chatEntries = new ArrayList<>();
        //conditionList.forEach(condition -> sessions.addAll(studyService.listSessionsByCondition(condition)));
        chatEntries.addAll(chatEntryDao.listChatEntryByChatScript(chatscriptId));
        model.addAttribute("chatentryList", chatEntries);

        return "chatscript/chatscript-display";
    }


    @GetMapping("/create")
    public String createChatScript(Model model) {

        ChatScript chatScript = new ChatScript();
        model.addAttribute("chatscript", chatScript);
        return "chatscript/chatscript-edit";
    }

    @GetMapping("{chatscriptId}/edit")
    public String editChatScript(@PathVariable("chatscriptId") Long chatscriptId, Model model) {

        ChatScript chatScript = new ChatScript(chatScriptDao.get(chatscriptId));
        model.addAttribute("chatscript", chatScript);
        return "chatscript/chatscript-edit";
    }

    @PostMapping
    public String saveChatScript(@ModelAttribute ChatScript chatScript, RedirectAttributes redirectAttributes) {

        if (chatScript.getId() == null) {
            chatScriptDao.create(chatScript);
            MessageUtils.addSuccessMessage("Chat Script created successfully!", redirectAttributes);
        } else {
            chatScriptDao.update(chatScript);
            MessageUtils.addSuccessMessage("Chat Script updated successfully!", redirectAttributes);
        }
        return "redirect:/admin/chatscripts";
    }

}
