package jmri.jmrit.operations.rollingstock;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import jmri.IdTag;
import jmri.InstanceManager;
import jmri.jmrit.operations.OperationsFrame;
import jmri.jmrit.operations.OperationsXml;
import jmri.jmrit.operations.locations.Location;
import jmri.jmrit.operations.locations.LocationManager;
import jmri.jmrit.operations.locations.Track;
import jmri.jmrit.operations.rollingstock.cars.CarColors;
import jmri.jmrit.operations.rollingstock.cars.CarEditFrame;
import jmri.jmrit.operations.rollingstock.cars.CarLengths;
import jmri.jmrit.operations.rollingstock.cars.CarLoads;
import jmri.jmrit.operations.rollingstock.cars.CarOwners;
import jmri.jmrit.operations.rollingstock.cars.CarRoads;
import jmri.jmrit.operations.rollingstock.cars.CarTypes;
import jmri.jmrit.operations.rollingstock.engines.EngineLengths;
import jmri.jmrit.operations.rollingstock.engines.EngineTypes;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.operations.setup.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Frame for edit of rolling stock. The common elements are: road, road number,
 * type, blocking, length, location and track, groups (Kernel or Consist)
 * weight, color, built, owner, comment.
 * 
 * The edit engine frame currently doesn't show blocking or color.
 * 
 * Engines and cars have different type, length, and group managers.
 *
 * @author Dan Boudreau Copyright (C) 2018
 */
public abstract class RollingStockEditFrame extends OperationsFrame implements java.beans.PropertyChangeListener {

    protected static final boolean IS_SAVE = true;

    protected RollingStock _rs;

    protected LocationManager locationManager = InstanceManager.getDefault(LocationManager.class);

    // labels
    JLabel textWeightOz = new JLabel(Bundle.getMessage("WeightOz"));
    JLabel textWeightTons = new JLabel(Bundle.getMessage("WeightTons"));

    // major buttons
    public JButton editRoadButton = new JButton(Bundle.getMessage("ButtonEdit"));
    public JButton clearRoadNumberButton = new JButton(Bundle.getMessage("ButtonClear"));
    public JButton editTypeButton = new JButton(Bundle.getMessage("ButtonEdit"));
    public JButton editColorButton = new JButton(Bundle.getMessage("ButtonEdit"));
    public JButton editLengthButton = new JButton(Bundle.getMessage("ButtonEdit"));
    public JButton fillWeightButton = new JButton(Bundle.getMessage("Calculate"));
    public JButton editLoadButton = new JButton(Bundle.getMessage("ButtonEdit"));
    public JButton editGroupButton = new JButton(Bundle.getMessage("ButtonEdit"));
    public JButton editOwnerButton = new JButton(Bundle.getMessage("ButtonEdit"));

    public JButton saveButton = new JButton(Bundle.getMessage("ButtonSave"));
    public JButton deleteButton = new JButton(Bundle.getMessage("ButtonDelete"));
    public JButton addButton = new JButton(Bundle.getMessage("ButtonAdd")); // have button state item to add

    // check boxes
    public JCheckBox autoWeightCheckBox = new JCheckBox(Bundle.getMessage("Auto"));
    public JCheckBox autoTrackCheckBox = new JCheckBox(Bundle.getMessage("Auto"));

    // text field
    public JTextField roadNumberTextField = new JTextField(Control.max_len_string_road_number);
    public JTextField blockingTextField = new JTextField(4);
    public JTextField builtTextField = new JTextField(Control.max_len_string_built_name + 3);
    public JTextField weightTextField = new JTextField(Control.max_len_string_weight_name);
    public JTextField weightTonsTextField = new JTextField(Control.max_len_string_weight_name);
    public JTextField commentTextField = new JTextField(35);
    public JTextField valueTextField = new JTextField(8);

    // combo boxes
    public JComboBox<String> roadComboBox = InstanceManager.getDefault(CarRoads.class).getComboBox();
    public JComboBox<String> typeComboBox = getTypeManager().getComboBox();
    public JComboBox<String> colorComboBox = InstanceManager.getDefault(CarColors.class).getComboBox();
    public JComboBox<String> lengthComboBox = getLengthManager().getComboBox();
    public JComboBox<String> ownerComboBox = InstanceManager.getDefault(CarOwners.class).getComboBox();
    public JComboBox<String> groupComboBox;
    public JComboBox<Location> locationBox = locationManager.getComboBox();
    public JComboBox<Track> trackLocationBox = new JComboBox<>();
    public JComboBox<String> loadComboBox = InstanceManager.getDefault(CarLoads.class).getComboBox(null);

    public JComboBox<IdTag> rfidComboBox = new JComboBox<>();

    // panels
    public JPanel pTypeOptions = new JPanel(); // options dependent on car or engine
    public JPanel pGroup = new JPanel(); // Kernel or Consist

    // panels for car edit
    public JPanel pBlocking = new JPanel();
    public JPanel pColor = new JPanel();
    public JPanel pLoad = new JPanel();
    public JPanel pWeightOz = new JPanel();

    public RollingStockEditFrame(String title) {
        super(title);
    }

    abstract protected RollingStockAttribute getTypeManager();

    abstract protected RollingStockAttribute getLengthManager();

    abstract protected void buttonEditActionPerformed(java.awt.event.ActionEvent ae);

    abstract protected ResourceBundle getRb();

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Checks for null")
    @Override
    public void initComponents() {

        // disable delete and save buttons
        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);

        editRoadButton.setToolTipText(MessageFormat.format(Bundle.getMessage("TipAddDeleteReplace"),
                new Object[]{Bundle.getMessage("road")})); // in OpsCarsBundle: initial caps for some languages i.e. German
        editTypeButton.setToolTipText(MessageFormat.format(Bundle.getMessage("TipAddDeleteReplace"),
                new Object[]{Bundle.getMessage("type")})); // initial caps for some languages i.e. German
        editColorButton.setToolTipText(MessageFormat.format(Bundle.getMessage("TipAddDeleteReplace"),
                new Object[]{Bundle.getMessage("Color").toLowerCase()}));
        editLengthButton.setToolTipText(MessageFormat.format(Bundle.getMessage("TipAddDeleteReplace"),
                new Object[]{Bundle.getMessage("length")})); // initial caps for some languages i.e. German
        editLoadButton.setToolTipText(MessageFormat.format(Bundle.getMessage("TipAddDeleteReplace"),
                new Object[]{Bundle.getMessage("load")})); // initial caps for some languages i.e. German
        editOwnerButton.setToolTipText(MessageFormat.format(Bundle.getMessage("TipAddDeleteReplace"),
                new Object[]{Bundle.getMessage("Owner").toLowerCase()}));
        editGroupButton.setToolTipText(MessageFormat.format(Bundle.getMessage("TipAddDeleteReplace"),
                new Object[]{Bundle.getMessage("Kernel").toLowerCase()}));

        // create panel
        JPanel pPanel = new JPanel();
        pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.Y_AXIS));

        // Layout the panel by rows
        // row 1
        JPanel pRoad = new JPanel();
        pRoad.setLayout(new GridBagLayout());
        pRoad.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Road")));
        addItem(pRoad, roadComboBox, 1, 0);
        addItem(pRoad, editRoadButton, 2, 0);
        pPanel.add(pRoad);

        // row 2
        JPanel pRoadNumber = new JPanel();
        pRoadNumber.setLayout(new GridBagLayout());
        pRoadNumber.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("RoadNumber")));
        addItem(pRoadNumber, roadNumberTextField, 1, 0);
        addItem(pRoadNumber, clearRoadNumberButton, 2, 0);
        pPanel.add(pRoadNumber);

        // row 3
        JPanel pType = new JPanel();
        pType.setLayout(new GridBagLayout());
        pType.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Type")));
        addItem(pType, typeComboBox, 0, 0);
        addItem(pType, editTypeButton, 1, 0);

        // type options dependent on car or engine rolling stock
        addItemWidth(pType, pTypeOptions, 3, 0, 1);
        pPanel.add(pType);

        // row 4
        pBlocking.setLayout(new GridBagLayout());
        pBlocking.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("BorderLayoutPassengerBlocking")));
        addItem(pBlocking, blockingTextField, 0, 0);
        blockingTextField.setText("0");
        // only cars use the blocking option
        pPanel.add(pBlocking);
        pBlocking.setVisible(false);

        // row 5
        JPanel pLength = new JPanel();
        pLength.setLayout(new GridBagLayout());
        pLength.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Length")));
        addItem(pLength, lengthComboBox, 1, 0);
        addItem(pLength, editLengthButton, 2, 0);
        pPanel.add(pLength);

        // row 6
        JPanel pLocation = new JPanel();
        pLocation.setLayout(new GridBagLayout());
        pLocation.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("LocationAndTrack")));
        addItem(pLocation, locationBox, 1, 0);
        addItem(pLocation, trackLocationBox, 2, 0);
        addItem(pLocation, autoTrackCheckBox, 3, 0);
        pPanel.add(pLocation);

        // optional panel
        JPanel pOptional = new JPanel();
        pOptional.setLayout(new BoxLayout(pOptional, BoxLayout.Y_AXIS));
        JScrollPane optionPane = new JScrollPane(pOptional);
        optionPane.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("BorderLayoutOptional")));

        // row 7
        JPanel pWeight = new JPanel();
        pWeight.setLayout(new BoxLayout(pWeight, BoxLayout.Y_AXIS));
        pWeight.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Weight")));

        // weight in oz only shown for cars
        pWeightOz.setLayout(new GridBagLayout());
        addItem(pWeightOz, textWeightOz, 0, 0);
        addItem(pWeightOz, weightTextField, 1, 0);
        addItem(pWeightOz, fillWeightButton, 2, 0);
        addItem(pWeightOz, autoWeightCheckBox, 3, 0);
        pWeight.add(pWeightOz);

        JPanel pWeightTons = new JPanel();
        pWeightTons.setLayout(new GridBagLayout());
        addItem(pWeightTons, textWeightTons, 0, 0);
        addItem(pWeightTons, weightTonsTextField, 1, 0);
        addItem(pWeightTons, new JLabel(), 2, 0);
        addItem(pWeightTons, new JLabel(), 3, 0);
        addItem(pWeightTons, new JLabel(), 4, 0);
        pWeight.add(pWeightTons);
        pOptional.add(pWeight);

        // row 8
        pColor.setLayout(new GridBagLayout());
        pColor.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Color")));
        addItem(pColor, colorComboBox, 1, 0);
        addItem(pColor, editColorButton, 2, 0);
        pOptional.add(pColor);

        // row 9
        JPanel pLoad = new JPanel();
        pLoad.setLayout(new GridBagLayout());
        pLoad.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Load")));
        addItem(pLoad, loadComboBox, 1, 0);
        addItem(pLoad, editLoadButton, 2, 0);
        pOptional.add(pLoad);

        // row 10 
        pGroup.setLayout(new GridBagLayout());
        addItem(pGroup, groupComboBox, 1, 0);
        addItem(pGroup, editGroupButton, 2, 0);
        pOptional.add(pGroup);

        // row 11
        JPanel pBuilt = new JPanel();
        pBuilt.setLayout(new GridBagLayout());
        pBuilt.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Built")));
        addItem(pBuilt, builtTextField, 1, 0);
        pOptional.add(pBuilt);

        // row 12
        JPanel pOwner = new JPanel();
        pOwner.setLayout(new GridBagLayout());
        pOwner.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Owner")));
        addItem(pOwner, ownerComboBox, 1, 0);
        addItem(pOwner, editOwnerButton, 2, 0);
        pOptional.add(pOwner);

        // row 13
        if (Setup.isValueEnabled()) {
            JPanel pValue = new JPanel();
            pValue.setLayout(new GridBagLayout());
            pValue.setBorder(BorderFactory.createTitledBorder(Setup.getValueLabel()));
            addItem(pValue, valueTextField, 1, 0);
            pOptional.add(pValue);
        }

        // row 14
        if (Setup.isRfidEnabled() && jmri.InstanceManager.getNullableDefault(jmri.IdTagManager.class) != null) {
            JPanel pRfid = new JPanel();
            pRfid.setLayout(new GridBagLayout());
            pRfid.setBorder(BorderFactory.createTitledBorder(Setup.getRfidLabel()));
            addItem(pRfid, rfidComboBox, 1, 0);
            jmri.InstanceManager.getDefault(jmri.IdTagManager.class).getNamedBeanSet()
                    .forEach((tag) -> rfidComboBox.addItem(tag));
            rfidComboBox.insertItemAt((jmri.IdTag) null, 0); // must have a blank entry, for no ID tag, and make it the default.
            rfidComboBox.setSelectedIndex(0);
            pOptional.add(pRfid);
        }

        // row 15
        JPanel pComment = new JPanel();
        pComment.setLayout(new GridBagLayout());
        pComment.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Comment")));
        addItem(pComment, commentTextField, 1, 0);
        pOptional.add(pComment);

        // button panel
        JPanel pButtons = new JPanel();
        pButtons.setLayout(new GridBagLayout());
        addItem(pButtons, deleteButton, 0, 25);
        addItem(pButtons, addButton, 1, 25);
        addItem(pButtons, saveButton, 3, 25);

        // add panels
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(pPanel);
        getContentPane().add(optionPane);
        getContentPane().add(pButtons);

        // setup buttons
        addEditButtonAction(editRoadButton);
        addButtonAction(clearRoadNumberButton);
        addEditButtonAction(editTypeButton);
        addEditButtonAction(editLengthButton);
        addEditButtonAction(editColorButton);
        addEditButtonAction(editGroupButton);
        addEditButtonAction(editOwnerButton);

        addButtonAction(deleteButton);
        addButtonAction(addButton);
        addButtonAction(saveButton);
        addButtonAction(fillWeightButton);
        addButtonAction(editLoadButton);

        // setup combobox
        addComboBoxAction(typeComboBox);
        addComboBoxAction(lengthComboBox);
        addComboBoxAction(locationBox);

        // get notified if combo box gets modified
        addPropertyChangeListeners();

        initMinimumSize(new Dimension(Control.panelWidth500, Control.panelHeight500));
    }

    protected void load(RollingStock rs) {
        _rs = rs;

        // enable delete and save buttons
        deleteButton.setEnabled(true);
        saveButton.setEnabled(true);

        // engines and cars share the same road database
        if (!InstanceManager.getDefault(CarRoads.class).containsName(rs.getRoadName())) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("roadNameNotExist"),
                    new Object[]{rs.getRoadName()}), Bundle.getMessage("addRoad"),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                InstanceManager.getDefault(CarRoads.class).addName(rs.getRoadName());
            }
        }
        roadComboBox.setSelectedItem(rs.getRoadName());

        roadNumberTextField.setText(rs.getNumber());

        if (!getTypeManager().containsName(rs.getTypeName())) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("typeNameNotExist"),
                    new Object[]{rs.getTypeName()}), Bundle.getMessage("addType"),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                getTypeManager().addName(rs.getTypeName());
            }
        }
        typeComboBox.setSelectedItem(rs.getTypeName());

        if (!getLengthManager().containsName(rs.getLength())) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("lengthNameNotExist"),
                    new Object[]{rs.getLength()}), Bundle.getMessage("addLength"),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                getLengthManager().addName(rs.getLength());
            }
        }
        //        }
        lengthComboBox.setSelectedItem(rs.getLength());

        weightTextField.setText(rs.getWeight());
        weightTonsTextField.setText(rs.getWeightTons());
        locationBox.setSelectedItem(rs.getLocation());
        updateTrackLocationBox();

        builtTextField.setText(rs.getBuilt());

        // Engines and cars share the owner database
        if (!InstanceManager.getDefault(CarOwners.class).containsName(rs.getOwner())) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("ownerNameNotExist"),
                    new Object[]{rs.getOwner()}), Bundle.getMessage("addOwner"),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                InstanceManager.getDefault(CarOwners.class).addName(rs.getOwner());
            }
        }
        ownerComboBox.setSelectedItem(rs.getOwner());

        commentTextField.setText(rs.getComment());
        valueTextField.setText(rs.getValue());
        rfidComboBox.setSelectedItem(rs.getIdTag());
        autoTrackCheckBox.setEnabled(true);
        blockingTextField.setText(Integer.toString(rs.getBlocking()));
    }

    // combo boxes
    //    @Override
    @Override
    public void comboBoxActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == locationBox) {
            updateTrackLocationBox();
        }
    }

    protected void updateTrackLocationBox() {
        if (locationBox.getSelectedItem() == null) {
            trackLocationBox.removeAllItems();
        } else {
            log.debug("Update tracks for location: " + locationBox.getSelectedItem());
            Location loc = ((Location) locationBox.getSelectedItem());
            loc.updateComboBox(trackLocationBox, _rs, autoTrackCheckBox.isSelected(), false);
            if (_rs != null && _rs.getLocation() == loc) {
                trackLocationBox.setSelectedItem(_rs.getTrack());
            }
        }
    }

    protected boolean check(RollingStock rs) {
        String roadNum = roadNumberTextField.getText();
        if (!OperationsXml.checkFileName(roadNum)) { // NOI18N
            JOptionPane.showMessageDialog(this,
                    Bundle.getMessage("NameResChar") + NEW_LINE + Bundle.getMessage("ReservedChar"),
                    Bundle.getMessage("roadNumNG"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (roadNum.length() > Control.max_len_string_road_number) {
            JOptionPane.showMessageDialog(this, MessageFormat.format(getRb().getString("RoadNumMustBeLess"),
                    new Object[]{Control.max_len_string_road_number + 1}), getRb().getString("RoadNumTooLong"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // check car's weight in tons has proper format
        try {
            Integer.parseInt(weightTonsTextField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, getRb().getString("WeightFormatTon"),
                    getRb().getString("WeightTonError"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected <T extends RollingStock> void save(RollingStockManager<T> manager, boolean isSave) {
        // if the rolling stock's road or number changes, it needs a new id
        if (isSave && _rs != null &&
                (!_rs.getRoadName().equals(roadComboBox.getSelectedItem()) ||
                        !_rs.getNumber().equals(roadNumberTextField.getText()))) {
            String road = (String) roadComboBox.getSelectedItem();
            String number = roadNumberTextField.getText();
            manager.changeId((T) _rs, road, number);
            _rs.setRoadName(road);
            _rs.setNumber(number);
        }
        if (_rs == null ||
                !_rs.getRoadName().equals(roadComboBox.getSelectedItem()) ||
                !_rs.getNumber().equals(roadNumberTextField.getText())) {
            _rs = manager.newRS((String) roadComboBox.getSelectedItem(), roadNumberTextField.getText());
            _rs.addPropertyChangeListener(this);
        }
        if (typeComboBox.getSelectedItem() != null) {
            _rs.setTypeName((String) typeComboBox.getSelectedItem());
        }
        if (lengthComboBox.getSelectedItem() != null) {
            _rs.setLength((String) lengthComboBox.getSelectedItem());
        }
        try {
            _rs.setWeight(NumberFormat.getNumberInstance().parse(weightTextField.getText()).toString());
        } catch (ParseException e1) {
            log.debug("Weight not a number");
        }
        _rs.setWeightTons(weightTonsTextField.getText());

        _rs.setComment(commentTextField.getText());
        _rs.setValue(valueTextField.getText());
        // save the IdTag for this rolling stock
        IdTag idTag = (IdTag) rfidComboBox.getSelectedItem();
        if (idTag != null) {
            _rs.setRfid(idTag.toString());
        }
        autoTrackCheckBox.setEnabled(true);

        if (locationBox.getSelectedItem() != null && trackLocationBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, getRb().getString("rsFullySelect"), getRb().getString("rsCanNotLoc"),
                    JOptionPane.ERROR_MESSAGE);
            // update location only if it has changed
        } else if (_rs.getLocation() == null ||
                !_rs.getLocation().equals(locationBox.getSelectedItem()) ||
                _rs.getTrack() == null ||
                !_rs.getTrack().equals(trackLocationBox.getSelectedItem())) {
            setLocationAndTrack(_rs);
        }
    }

    protected void setLocationAndTrack(RollingStock rs) {
        if (locationBox.getSelectedItem() == null) {
            rs.setLocation(null, null);
        } else {
            rs.setLastRouteId(RollingStock.NONE); // clear last route id
            String status = rs.setLocation((Location) locationBox.getSelectedItem(), (Track) trackLocationBox
                    .getSelectedItem());
            if (!status.equals(Track.OKAY)) {
                log.debug("Can't set rolling stock's location because of {}", status);
                JOptionPane.showMessageDialog(this, MessageFormat.format(getRb().getString("rsCanNotLocMsg"),
                        new Object[]{rs.toString(), status}), getRb().getString("rsCanNotLoc"),
                        JOptionPane.ERROR_MESSAGE);
                // does the user want to force the rolling stock to this track?
                int results = JOptionPane.showOptionDialog(this, MessageFormat.format(getRb().getString("rsForce"),
                        new Object[]{rs.toString(), (Track) trackLocationBox.getSelectedItem()}),
                        MessageFormat
                                .format(getRb().getString("rsOverride"), new Object[]{status}),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (results == JOptionPane.YES_OPTION) {
                    log.debug("Force rolling stock to track");
                    rs.setLocation((Location) locationBox.getSelectedItem(), (Track) trackLocationBox
                            .getSelectedItem(), RollingStock.FORCE);
                }
            }
        }
    }

    // for the AttributeEditFrame edit buttons
    private void addEditButtonAction(JButton b) {
        b.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                buttonEditActionPerformed(e);
            }
        });
    }

    @Override
    public void dispose() {
        removePropertyChangeListeners();
        super.dispose();
    }

    private void addPropertyChangeListeners() {
        InstanceManager.getDefault(CarRoads.class).addPropertyChangeListener(this);
        InstanceManager.getDefault(CarLoads.class).addPropertyChangeListener(this);
        getTypeManager().addPropertyChangeListener(this);
        getLengthManager().addPropertyChangeListener(this);
        InstanceManager.getDefault(CarColors.class).addPropertyChangeListener(this);
        InstanceManager.getDefault(CarOwners.class).addPropertyChangeListener(this);
        locationManager.addPropertyChangeListener(this);
    }

    private void removePropertyChangeListeners() {
        InstanceManager.getDefault(CarRoads.class).removePropertyChangeListener(this);
        InstanceManager.getDefault(CarLoads.class).removePropertyChangeListener(this);
        getTypeManager().removePropertyChangeListener(this);
        getLengthManager().removePropertyChangeListener(this);
        InstanceManager.getDefault(CarColors.class).removePropertyChangeListener(this);
        InstanceManager.getDefault(CarOwners.class).removePropertyChangeListener(this);
        locationManager.removePropertyChangeListener(this);
        if (_rs != null) {
            _rs.removePropertyChangeListener(this);
        }
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        if (e.getPropertyName().equals(CarRoads.CARROADS_CHANGED_PROPERTY)) {
            InstanceManager.getDefault(CarRoads.class).updateComboBox(roadComboBox);
            if (_rs != null) {
                roadComboBox.setSelectedItem(_rs.getRoadName());
            }
        }
        if (e.getPropertyName().equals(CarTypes.CARTYPES_CHANGED_PROPERTY) ||
                e.getPropertyName().equals(EngineTypes.ENGINETYPES_CHANGED_PROPERTY)) {
            getTypeManager().updateComboBox(typeComboBox);
            if (_rs != null) {
                typeComboBox.setSelectedItem(_rs.getTypeName());
            }
        }
        if (e.getPropertyName().equals(CarColors.CARCOLORS_CHANGED_PROPERTY)) {
            InstanceManager.getDefault(CarColors.class).updateComboBox(colorComboBox);
            if (_rs != null) {
                colorComboBox.setSelectedItem(_rs.getColor());
            }
        }
        if (e.getPropertyName().equals(CarLengths.CARLENGTHS_CHANGED_PROPERTY) ||
                e.getPropertyName().equals(EngineLengths.ENGINELENGTHS_CHANGED_PROPERTY)) {
            InstanceManager.getDefault(CarLengths.class).updateComboBox(lengthComboBox);
            if (_rs != null) {
                lengthComboBox.setSelectedItem(_rs.getLength());
            }
        }
        if (e.getPropertyName().equals(CarOwners.CAROWNERS_CHANGED_PROPERTY)) {
            InstanceManager.getDefault(CarOwners.class).updateComboBox(ownerComboBox);
            if (_rs != null) {
                ownerComboBox.setSelectedItem(_rs.getOwner());
            }
        }
        if (e.getPropertyName().equals(LocationManager.LISTLENGTH_CHANGED_PROPERTY) ||
                e.getPropertyName().equals(RollingStock.TRACK_CHANGED_PROPERTY)) {
            InstanceManager.getDefault(LocationManager.class).updateComboBox(locationBox);
            updateTrackLocationBox();
            if (_rs != null && _rs.getLocation() != null) {
                locationBox.setSelectedItem(_rs.getLocation());
            }
        }
    }

    private final static Logger log = LoggerFactory.getLogger(CarEditFrame.class);
}