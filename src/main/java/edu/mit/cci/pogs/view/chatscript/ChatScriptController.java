package edu.mit.cci.pogs.view.chatscript;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.dao.chatscript.ChatScriptDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;
import edu.mit.cci.pogs.service.ChatScriptService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.chatscript.beans.ChatEntriesBean;
import edu.mit.cci.pogs.view.chatscript.beans.ChatScriptBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
//@RequestMapping(value = "/admin/chatscripts")
public class ChatScriptController {

    @Autowired
    private ChatScriptDao chatScriptDao;

    @Autowired
    private ChatEntryDao chatEntryDao;

    @Autowired
    private ChatScriptService chatScriptService;

    @GetMapping("/admin/chatscripts")
    public String getChatScript1(Model model) {

        model.addAttribute("chatscriptList", chatScriptDao.list());
        return "chatscript/chatscript-list";
    }

    @GetMapping("/chatscripts/")
    public String getChatScript(Model model) {
         model.addAttribute("chatscriptList", chatScriptDao.list());
        return "chatscript/chatscript-list";
    }

    @GetMapping("/admin/chatscripts/{chatscriptId}")
    public String getChatscripts(@PathVariable("chatscriptId") Long chatscriptId, Model model) {
        ChatScript chatScript = chatScriptDao.get(chatscriptId);
        model.addAttribute("chatscript", chatScript);
        ChatEntriesBean chatEntriesBean = new ChatEntriesBean();
        chatEntriesBean.setChatEntryList(chatScriptService.listChatEntriesByChatscriptId(chatscriptId));
        model.addAttribute("chatEntriesBean", chatEntriesBean);

        return "chatscript/chatscript-display";
    }

    @GetMapping("admin/chatscripts/create")
    public String createChatScript(Model model) {

        ChatScriptBean chatScriptBean = new ChatScriptBean();
        ChatScript chatScript = new ChatScript();
        model.addAttribute("chatscript",chatScript);
        model.addAttribute("chatscriptBean", chatScriptBean);
        return "chatscript/chatscript-edit";
    }

    @GetMapping("/admin/chatscripts/{chatscriptId}/edit")
    public String editChatScript(@PathVariable("chatscriptId") Long chatscriptId, Model model) {

        ChatScriptBean chatScript = new ChatScriptBean();
        chatScript.setId(chatscriptId);
        model.addAttribute("chatscript", chatScript);
        return "chatscript/chatscript-edit";
    }

    @PostMapping("/admin/chatscripts/chatentries/edit")
    public String saveChatEntry(@ModelAttribute ChatEntriesBean chatEntriesBean, RedirectAttributes redirectAttributes) {

        chatScriptService.updateChatEntryList(chatEntriesBean);
        return "redirect:/admin/chatscripts/" + chatEntriesBean.getChatscriptId();
    }

    @PostMapping("/admin/chatscripts")
    public String saveChatScript(@ModelAttribute ChatScript chatscript, RedirectAttributes redirectAttributes) {

        if (chatscript.getId() == null) {
            chatscript = chatScriptDao.create(chatscript);
            MessageUtils.addSuccessMessage("Chat Script created successfully!", redirectAttributes);
        } else {
            chatScriptDao.update(chatscript);
            MessageUtils.addSuccessMessage("Chat Script updated successfully!", redirectAttributes);
        }
        return "redirect:/admin/chatscripts/"+chatscript.getId();
    }

    @GetMapping("/admin/chatscripts/{id}/chatentries/edit")
    public String editChatEntries(@PathVariable("id") Long id, Model model) {
        ChatScript chatScript = new ChatScript(chatScriptDao.get(id));

        ChatEntriesBean chatEntriesBean = new ChatEntriesBean();
        chatEntriesBean.setChatEntryList(chatScriptService.listChatEntriesByChatscriptId(id));

        model.addAttribute("chatscript", chatScript);
        model.addAttribute("chatEntriesBean", chatEntriesBean);
        return "chatscript/chatentry-edit";
    }
}
