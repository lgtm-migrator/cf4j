package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CorrelationConstrainedTest {

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void similarity() {
    CorrelationConstrained sim = new CorrelationConstrained(0.5);
    sim.setDatamodel(datamodel);
    sim.beforeRun();

    Item item0 = datamodel.getItem(0);
    Item item1 = datamodel.getItem(1);
    Item item2 = datamodel.getItem(3);

    assertEquals(0.8320502943378437, sim.similarity(item0, item1));
    assertEquals(1.0, sim.similarity(item0, item2));
    sim.afterRun();
  }
}
