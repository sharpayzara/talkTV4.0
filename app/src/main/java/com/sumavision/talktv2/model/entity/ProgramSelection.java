package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sharpay on 16-6-28.
 */
public class ProgramSelection extends BaseData{

    private List<ItemsBean> items;

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public class ItemsBean implements Serializable{
        private String name;
        private String params;

        private List<ItemsBean2> items;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public List<ItemsBean2> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean2> items) {
            this.items = items;
        }

        public class ItemsBean2 implements Serializable{
            private String title;
            private String value;
            private String params;

            public String getParams() {
                return params;
            }

            public void setParams(String params) {
                this.params = params;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
