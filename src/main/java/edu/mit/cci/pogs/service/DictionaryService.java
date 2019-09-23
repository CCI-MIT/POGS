package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.dictionaryhasresearchgroup.DictionaryHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryBean;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.utils.ColorUtils;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryEntriesBean;

@Service
public class DictionaryService {

    private final DictionaryDao dictionaryDao;
    private final DictionaryEntryDao dictionaryEntryDao;
    private final Environment env;

    private final TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;
    private final TaskConfigurationDao taskConfigurationDao;

    private final DictionaryHasResearchGroupDao dictionaryHasResearchGroupDao;

    @Autowired
    public DictionaryService(DictionaryDao dictionaryDao, DictionaryEntryDao dictionaryEntryDao,
                             Environment env, TaskHasTaskConfigurationDao taskHasTaskConfigurationDao,
    TaskConfigurationDao taskConfigurationDao, DictionaryHasResearchGroupDao dictionaryHasResearchGroupDao) {
        this.dictionaryDao = dictionaryDao;
        this.dictionaryEntryDao = dictionaryEntryDao;
        this.env = env;
        this.taskHasTaskConfigurationDao = taskHasTaskConfigurationDao;
        this.taskConfigurationDao = taskConfigurationDao;
        this.dictionaryHasResearchGroupDao =dictionaryHasResearchGroupDao;
    }

    public void updateDictionaryEntryList(DictionaryEntriesBean dictionaryEntriesBean) {
        List<DictionaryEntry> dictionaryEntryList = dictionaryEntriesBean.getDictionaryEntryList();
        List<DictionaryEntry> existingDictEntries = dictionaryEntryDao.listDictionaryEntriesByDictionary(dictionaryEntriesBean.getDictionaryId());
        Map<Long, DictionaryEntry> existingMap = new HashMap<>();
        existingDictEntries.stream().forEach(a -> existingMap.put(a.getId(),a));

        for (DictionaryEntry dictEntry : dictionaryEntryList) {
            dictEntry.setDictionaryId(dictionaryEntriesBean.getDictionaryId());
            if(dictEntry.getId()!=null){
                dictionaryEntryDao.update(dictEntry);
                existingMap.remove(dictEntry.getId());
            }else{
                dictionaryEntryDao.create(dictEntry);
            }
        }

        for (DictionaryEntry dictEnt: existingMap.values()){
            dictionaryEntryDao.deleteDictionaryEntry(dictEnt);
        }
    }

    public JSONArray listDictionaryEntriesJson(Long dictionaryId) {

        List<DictionaryEntry> taskExecutionAttributes = dictionaryEntryDao
                .listDictionaryEntriesByDictionary(dictionaryId);
        JSONArray configurationArray = new JSONArray();
        for (DictionaryEntry tea : taskExecutionAttributes) {
            JSONObject teaJson = new JSONObject();
            teaJson.put("entryType", tea.getEntryType());
            teaJson.put("entryCategory", tea.getEntryCategory());
            teaJson.put("entryValue", tea.getEntryValue());
            configurationArray.put(teaJson);
        }
        return configurationArray;
    }
    public List<DictionaryHasResearchGroup> listDictionaryHasResearchGroupByDictionaryId(Long dictionaryId) {
        return this.dictionaryHasResearchGroupDao.listByDictionaryId(dictionaryId);
    }
    private void createOrUpdateUserGroups(DictionaryBean dictionaryBean) {
        if (dictionaryBean.getResearchGroupRelationshipBean() == null && dictionaryBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }
        List<DictionaryHasResearchGroup> toCreate = new ArrayList<>();
        List<DictionaryHasResearchGroup> toDelete = new ArrayList<>();
        List<DictionaryHasResearchGroup> currentlySelected = listDictionaryHasResearchGroupByDictionaryId(dictionaryBean.getId());
        
        for (DictionaryHasResearchGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : dictionaryBean.getResearchGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }
            
        }
        
        
        for (String researchGroupId : dictionaryBean.getResearchGroupRelationshipBean().getSelectedValues()) {
            
            boolean selectedAlreadyIn = false;
            for (DictionaryHasResearchGroup rghau : currentlySelected) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                DictionaryHasResearchGroup rghau = new DictionaryHasResearchGroup();
                rghau.setDictionaryId(dictionaryBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }
            
        }
        for (DictionaryHasResearchGroup toCre : toCreate) {
            dictionaryHasResearchGroupDao.create(toCre);
        }
        for (DictionaryHasResearchGroup toDel : toDelete) {
            dictionaryHasResearchGroupDao.delete(toDel);
        }
        
    }
    
    public Dictionary createOrUpdate(DictionaryBean dictionaryBean) {
        
        Dictionary dictionary = new Dictionary();
        dictionary.setId(dictionaryBean.getId());
        dictionary.setDictionaryName(dictionaryBean.getDictionaryName());
        dictionary.setHasGroundTruth(dictionaryBean.getHasGroundTruth() == null ? false : dictionaryBean.getHasGroundTruth());
        
        if (dictionary.getId() == null) {
            dictionary = dictionaryDao.create(dictionary);
            dictionaryBean.setId(dictionary.getId());
            createOrUpdateUserGroups(dictionaryBean);
        } else {
            dictionaryDao.update(dictionary);
            createOrUpdateUserGroups(dictionaryBean);
        }
        return dictionary;
        
    }
    public JSONObject getDictionaryJSONObjectForTaskPlugin(long pluginConfigId) {
        TaskConfiguration tc = taskConfigurationDao.getByTaskPluginConfigurationId(pluginConfigId);
        JSONObject jo = getJsonObjectFromTaskConfig(tc);
        if (jo != null) return jo;
        return new JSONObject();
    }

    public JSONObject getDictionaryJSONObjectForTask(Long taskId){
        TaskHasTaskConfiguration configuration = taskHasTaskConfigurationDao
                .getByTaskId(taskId);
        TaskConfiguration tg = taskConfigurationDao.get(configuration.getTaskConfigurationId());
        JSONObject jo = getJsonObjectFromTaskConfig(tg);
        if (jo != null) return jo;
        return null;
    }

    private JSONObject getJsonObjectFromTaskConfig(TaskConfiguration tg) {
        if(tg!=null){
            if(tg.getDictionaryId()!=null) {

                Dictionary dict = dictionaryDao.get(tg.getDictionaryId());
                List<DictionaryEntry> entries = dictionaryEntryDao.listDictionaryEntriesByDictionary(tg.getDictionaryId());
                JSONArray ja = new JSONArray();
                if(entries!=null &&! entries.isEmpty()){
                    for(DictionaryEntry de: entries){
                        ja.put(de.getId());
                    }
                }
                if(dict!=null){
                    JSONObject jo = new JSONObject();
                    jo.put("id", dict.getId());
                    jo.put("hasGroundTruth",dict.getHasGroundTruth());
                    jo.put("dictionaryName",dict.getDictionaryName());
                    jo.put("dictionaryEntries", ja);
                    return jo;
                }
            }
        }
        return null;
    }

    @Deprecated
    public File generateImageFromDictionary(HttpServletRequest request, Long dictionaryId, String backgroundColor){

        Dictionary dict = dictionaryDao.get(dictionaryId);
        List<DictionaryEntry> entries = dictionaryEntryDao
                .listDictionaryEntriesByDictionary(dictionaryId);

        StringBuffer text = new StringBuffer();
        for(DictionaryEntry de: entries){
            text.append(de.getEntryValue());
        }

        List<WordPlacement> list = new ArrayList<>();
        int currentLine = 0;
        int currentLineWidth = 0;
        for(String token: text.toString().split(" ")) {

        }


        /*
           Because font metrics is based on a graphics context, we need to create
           a small, temporary image so we can ascertain the width and height
           of the final image
         */
        int IMAGE_WIDTH = 350;
        int LINE_HEIGHT = 0;
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 8);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text.toString());
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color bgColor = ColorUtils.decodeHtmlColorString(backgroundColor);
        Color color = ColorUtils.generateFontColorBasedOnBackgroundColor(bgColor);
        g2d.setBackground(bgColor);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(color);

        g2d.drawString(text.toString(), 0, fm.getAscent());


        g2d.dispose();

        String path = env.getProperty("images.dir");
        if(path == null ){
            path = request.getSession().getServletContext().getRealPath("/");
        }
        String finalPath = path + "/fileEntries/" + File.separator;


        try {
            File file = new File(finalPath+"dictionary_"+dictionaryId+".png");
            file.mkdirs();
            ImageIO.write(img, "png", file);
            return file;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;

    }


}
class WordPlacement {
    private String text;
    private int width;
    private int line;
    public WordPlacement(String text){
        this.text = text;

    }
}
