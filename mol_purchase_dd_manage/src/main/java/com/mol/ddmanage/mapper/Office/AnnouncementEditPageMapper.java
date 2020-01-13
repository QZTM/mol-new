package com.mol.ddmanage.mapper.Office;

import com.mol.ddmanage.Ben.Office.AnnouncementEditPageben;

public interface AnnouncementEditPageMapper
{
    void Announcement(AnnouncementEditPageben json);
    void AddNews(String id, String title, String introduction, String main_text, String creation_time, String img);
}
