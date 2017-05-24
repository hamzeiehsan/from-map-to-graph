package hamzei.ehsan.spatial;

import hamzei.ehsan.model.BoundingBox;
import hamzei.ehsan.model.DBModel;
import hamzei.ehsan.model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ehsan Hamzei on 5/22/2017.
 */
public class SpatialUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SpatialUtil.class);
//    static {
//        PropertyConfigurator.configure("src/main/resource/log4j.properties");
//    }

    public static boolean isNeighbour (DBModel model, DBModel otherModel, List<DBModel> models) {
        for (DBModel refModel : models) {
            if (!refModel.equals(model) && !refModel.equals(otherModel)) {
                if (!isNeighbour(model.getBox(), otherModel.getBox(), refModel.getBox()))
                    return false;
            }
        }
        return true;
    }

    public static boolean isNeighbour(BoundingBox geometry1, BoundingBox geometry2, BoundingBox referenceGeometry) {
        return !isContained(combinedBoundingBox(geometry1, geometry2), referenceGeometry);
    }

    public static boolean isContained(BoundingBox geometry1, BoundingBox geometry2) {
        if (geometry1.getNW().getLon() >= geometry2.getNW().getLon() && geometry1.getNW().getLat() >= geometry2.getNW().getLat()
                && geometry1.getSE().getLon() <= geometry2.getSE().getLon() && geometry1.getSE().getLat() <= geometry2.getSE().getLat())
            return true;
        return false;
    }

    public static BoundingBox combinedBoundingBox(BoundingBox geometry1, BoundingBox geometry2) {
        List<Point> points = new ArrayList<Point>();
        points.add(new Point(geometry1.getNW().getLat(), geometry1.getNW().getLon()));
        points.add(new Point(geometry2.getNW().getLat(), geometry2.getNW().getLon()));
        points.add(new Point(geometry1.getSE().getLat(), geometry1.getSE().getLon()));
        points.add(new Point(geometry2.getSE().getLat(), geometry2.getSE().getLon()));
        return new BoundingBox(points);
    }

    public static List<DirectionRelations> calculateRelations(BoundingBox geometry1, BoundingBox geometry2) {
        List<DirectionRelations> relations = new ArrayList<DirectionRelations>();
        BoundingBox boundingBox = combinedBoundingBox(geometry1, geometry2);
        //WEST CHECK
        if (isEqual(boundingBox.getNW().getLon(), geometry1.getNW().getLon()) && !isEqual(boundingBox.getNW().getLon(), geometry2.getNW().getLon())) {
            LOG.debug("WEST CHECK: 1");
            //NORTH CHECK
            if (isEqual(boundingBox.getNW().getLat(), geometry1.getNW().getLat()) && !isEqual(boundingBox.getNW().getLat(), geometry2.getNW().getLat())) {
                LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1");
                // EAST CHECK
                if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 1");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 2");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.N);
                        relations.add(DirectionRelations.W);
                    } else {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.W);
                    }
                } else {
                    LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 3");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.N);
                    } else {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                }
            } else if (!isEqual(boundingBox.getNW().getLat(), geometry1.getNW().getLat()) && isEqual(boundingBox.getNW().getLat(), geometry2.getNW().getLat())) {
                LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2");
                // EAST CHECK
                if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 1");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 2");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.S);
                        relations.add(DirectionRelations.W);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.W);
                    }
                } else {
                    LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 3");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.S);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                }
            } else {
                {
                    LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3");
                    // EAST CHECK
                    if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 1");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.unknown);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.unknown);
                        } else {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 2");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.W);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.W);
                        } else {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    } else {
                        LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 3");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.unknown);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.unknown);
                        } else {
                            LOG.debug("WEST CHECK: 1 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    }
                }
            }
        } else if (!isEqual(boundingBox.getNW().getLon(), geometry1.getNW().getLon()) && isEqual(boundingBox.getNW().getLon(), geometry2.getNW().getLon())) {
            LOG.debug("WEST CHECK: 2");
            //NORTH CHECK
            if (isEqual(boundingBox.getNW().getLat(), geometry1.getNW().getLat()) && !isEqual(boundingBox.getNW().getLat(), geometry2.getNW().getLat())) {
                LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1");
                // EAST CHECK
                if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 1");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.N);
                        relations.add(DirectionRelations.E);
                    } else {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.E);
                    }
                } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 2");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else {
                    LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 3");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.N);
                    } else {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                }
            } else if (!isEqual(boundingBox.getNW().getLat(), geometry1.getNW().getLat()) && isEqual(boundingBox.getNW().getLat(), geometry2.getNW().getLat())) {
                LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2");
                // EAST CHECK
                if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 1");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.S);
                        relations.add(DirectionRelations.E);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.E);
                    }
                } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 2");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else {
                    LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 3");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.S);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                }
            } else {
                {
                    LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3");
                    // EAST CHECK
                    if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 1");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.S);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.E);
                        } else {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 2");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.unknown);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.unknown);
                        } else {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    } else {
                        LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 3");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.unknown);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.unknown);
                        } else {
                            LOG.debug("WEST CHECK: 2 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    }
                }
            }
        } else {
            LOG.debug("WEST CHECK: 3");
            //NORTH CHECK
            if (isEqual(boundingBox.getNW().getLat(), geometry1.getNW().getLat()) && !isEqual(boundingBox.getNW().getLat(), geometry2.getNW().getLat())) {
                LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1");
                // EAST CHECK
                if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 1");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.N);
                    } else {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 2");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.N);
                    } else {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else {
                    LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 3");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 1 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                }
            } else if (!isEqual(boundingBox.getNW().getLat(), geometry1.getNW().getLat()) && isEqual(boundingBox.getNW().getLat(), geometry2.getNW().getLat())) {
                LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2");
                // EAST CHECK
                if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 1");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.S);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                    LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 2");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.S);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                } else {
                    LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 3");
                    //SOUTH CHECK
                    if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                        relations.add(DirectionRelations.unknown);
                    } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                        relations.add(DirectionRelations.unknown);
                    } else {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 2 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                        relations.add(DirectionRelations.unknown);
                    }
                }
            } else {
                {
                    LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3");
                    // EAST CHECK
                    if (isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && !isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 1");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.unknown);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.unknown);
                        } else {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 1 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    } else if (!isEqual(boundingBox.getSE().getLon(), geometry1.getSE().getLon()) && isEqual(boundingBox.getSE().getLon(), geometry2.getSE().getLon())) {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 2");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.unknown);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.unknown);
                        } else {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 2 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    } else {
                        LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 3");
                        //SOUTH CHECK
                        if (isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && !isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 1");
                            relations.add(DirectionRelations.unknown);
                        } else if (!isEqual(boundingBox.getSE().getLat(), geometry1.getSE().getLat()) && isEqual(boundingBox.getSE().getLat(), geometry2.getSE().getLat())) {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 2");
                            relations.add(DirectionRelations.unknown);
                        } else {
                            LOG.debug("WEST CHECK: 3 -- NORTH CHECK : 3 -- EAST CHECK : 3 -- SOUTH CHECK : 3");
                            relations.add(DirectionRelations.unknown);
                        }
                    }
                }
            }
        }

        return relations;
    }

    public static void main(String[] args) {
        List<Point> points1 = new ArrayList<Point>();
        points1.add(new Point(10, 10));
        points1.add(new Point(15, 15));
        BoundingBox b1 = new BoundingBox(points1);

        List<Point> points2 = new ArrayList<Point>();
        points2.add(new Point(20, 20));
        points2.add(new Point(25, 25));
        BoundingBox b2 = new BoundingBox(points2);

        List<Point> refPoint = new ArrayList<Point>();
        refPoint.add(new Point(17, 17));
        refPoint.add(new Point(29, 29));
        BoundingBox refB = new BoundingBox(refPoint);

        System.out.println(combinedBoundingBox(b1, b2));
        System.out.println(isNeighbour(b1, b2, refB));
        calculateRelations(b1, b2);
        System.out.println("--------------------");
        System.out.println(calculateRelations(b2, b1));
        System.out.println(calculateRelations(b1, b2));
        System.out.println(calculateRelations(b2, b2));
    }

    private static boolean isEqual(Double d1, Double d2) {
        if (Math.abs(d1 - d2) < 0.000001)
            return true;
        return false;
    }
}