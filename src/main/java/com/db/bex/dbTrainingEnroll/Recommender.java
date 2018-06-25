package com.db.bex.dbTrainingEnroll;

import com.db.bex.dbTrainingEnroll.dao.TrainingRepository;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EnableAsync
public class Recommender {

    private TrainingRepository trainingRepository;
    private DataSource dataSource;

    public Recommender(TrainingRepository trainingRepository, DataSource dataSource) {
        this.trainingRepository = trainingRepository;
        this.dataSource = dataSource;
    }

    @Async
    public List<Long> recommendTraining(Long idUser, int itemsRecommended){
        List<Long> list = null;
        try {
            list = new ArrayList<>();
            DataModel model = new MySQLJDBCDataModel(dataSource,"rating","user_id",
                    "training_id","rating",null);
            ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model, similarity);
            CachingRecommender recommender1 = new CachingRecommender(recommender);
            List<RecommendedItem> recommendedItemList = recommender1.recommend(idUser,itemsRecommended);
            for(RecommendedItem i : recommendedItemList)
                list.add(i.getItemID());
        } catch (TasteException e) {
            e.printStackTrace();
        }
        return list;
    }
}
