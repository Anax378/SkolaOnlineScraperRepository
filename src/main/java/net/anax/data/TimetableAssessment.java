package net.anax.data;

import org.jsoup.nodes.Element;

public class TimetableAssessment {
    public String showPopupWindowURL;
    public String subject;
    public FurtherInfoElement[] furtherInfo;

    public TimetableAssessment(int infoLength){
        furtherInfo = new FurtherInfoElement[infoLength];
    }

    public static TimetableAssessment parseAssessment(Element td){
        Element img = td.select("> table > tbody > tr > td > img").first();
        if(img == null){return null;}

        String attr = img.attr("onclick");
        TimetableAssessment assessment = getBlankAssessment();
        if(attr != null){
            attr = attr.replace("showPopupWindow('", "");
            if(!attr.isEmpty()){
                attr = attr.substring(0, attr.length()-1);
                int urlEnd = attr.indexOf("'");
                attr = attr.substring(0, urlEnd);
                assessment.showPopupWindowURL = attr;
            }
        }

        String mouseover = img.attr("onmouseover");
        if(mouseover != null){
            mouseover = mouseover.replace("onMouseOverTooltip('", "");
            if(mouseover.length() > 2){
                mouseover = mouseover.substring(0, mouseover.length()-2);
                String[] params = mouseover.split("' ?, ?'");
                assessment.subject  = params[0];
                if(params.length > 1){
                    String[] info = params[1].split("~");
                    FurtherInfoElement[] elements = new FurtherInfoElement[info.length/2];
                    for(int i = 0; i < info.length; i+=2){
                        elements[i/2] = new FurtherInfoElement(info[i], info[i+1].replaceAll("<br/?>", " "));
                    }
                    assessment.furtherInfo = elements;
                }
            }
        }
        return assessment;
    }

    public static TimetableAssessment getBlankAssessment(){
        TimetableAssessment assessment = new TimetableAssessment(0);
        assessment.showPopupWindowURL  = "";
        return assessment;
    }

    public static boolean isAssessment(Element td){
        Element img = td.select("> table > tbody > tr > td > img").first();
        return img != null;

    }
}