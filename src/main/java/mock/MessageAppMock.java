package mock;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class MessageAppMock {

    protected JFrame mFrame;

    protected DbConnector mDbConnector;

    protected IDataManager mDataManager;

    public MessageAppMock(DbConnector dbConnector, IDataManager dataManager) {
        this.mDbConnector = dbConnector;
        this.mDataManager = dataManager;
    }

    public void showGUI() {
        if (mFrame == null) {
            this.initGUI();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = MessageAppMock.this.mFrame;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((screenSize.width - frame.getWidth()) / 6,
                    (screenSize.height - frame.getHeight()) / 4);

            MessageAppMock.this.mFrame.setVisible(true);

            MessageAppMock.this.mFrame.pack();
        });
    }

    protected void initGUI() {
        mFrame = new JFrame("MOCK");
        mFrame.setLayout(new GridBagLayout());

        JLabel dbLabel = new JLabel("Database");

        Button addUserButton = new Button("Add User");
        addUserButton.setPreferredSize(new Dimension(100, 50));
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MessageAppMock.this.addUserInDatabase();
            }
        });

        Button addMessageButton = new Button("Add Message");
        addMessageButton.setPreferredSize(new Dimension(100, 50));
        addMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MessageAppMock.this.addMessageInDatabase();
            }
        });

        Button addChannelButton = new Button("Add Channel");
        addChannelButton.setPreferredSize(new Dimension(100, 50));
        addChannelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MessageAppMock.this.addChannelInDatabase();
            }
        });

        Button removeUserButton = new Button("Remove User");
        removeUserButton.setPreferredSize(new Dimension(100, 50));
        removeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.Set<User> users = MessageAppMock.this.mDataManager.getUsers();
                if (users.isEmpty()) return;
                ArrayList<User> list = new ArrayList<>(users);
                User chosen = list.get(new Random().nextInt(list.size()));
                MessageAppMock.this.mDataManager.deleteUserFile(chosen);
            }
        });

        Button removeMessageButton = new Button("Remove Message");
        removeMessageButton.setPreferredSize(new Dimension(100, 50));
        removeMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.Set<Message> messages = MessageAppMock.this.mDataManager.getMessages();
                if (messages.isEmpty()) return;
                ArrayList<Message> list = new ArrayList<>(messages);
                Message chosen = list.get(new Random().nextInt(list.size()));
                MessageAppMock.this.mDataManager.deleteMessageFile(chosen);
            }
        });

        Button removeChannelButton = new Button("Remove Channel");
        removeChannelButton.setPreferredSize(new Dimension(100, 50));
        removeChannelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.Set<Channel> channels = MessageAppMock.this.mDataManager.getChannels();
                if (channels.isEmpty()) return;
                ArrayList<Channel> list = new ArrayList<>(channels);
                Channel chosen = list.get(new Random().nextInt(list.size()));
                MessageAppMock.this.mDataManager.deleteChannelFile(chosen);
            }
        });

        Button updateUserButton = new Button("Update User");
        updateUserButton.setPreferredSize(new Dimension(100, 50));
        updateUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.Set<User> users = MessageAppMock.this.mDataManager.getUsers();
                if (users.isEmpty()) return;
                ArrayList<User> list = new ArrayList<>(users);
                User chosen = list.get(new Random().nextInt(list.size()));
                chosen.setName(chosen.getName() + "_upd");
                MessageAppMock.this.mDataManager.sendUser(chosen);
            }
        });

        Button updateMessageButton = new Button("Update Message");
        updateMessageButton.setPreferredSize(new Dimension(100, 50));
        updateMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.Set<Message> messages = MessageAppMock.this.mDataManager.getMessages();
                if (messages.isEmpty()) return;
                ArrayList<Message> list = new ArrayList<>(messages);
                Message chosen = list.get(new Random().nextInt(list.size()));
                Message updated = new Message(chosen.getUuid(), chosen.getSender(), chosen.getRecipient(), chosen.getEmissionDate(), chosen.getText() + "_upd");
                MessageAppMock.this.mDataManager.sendMessage(updated);
            }
        });

        Button updateChannelButton = new Button("Update Channel");
        updateChannelButton.setPreferredSize(new Dimension(100, 50));
        updateChannelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.Set<Channel> channels = MessageAppMock.this.mDataManager.getChannels();
                if (channels.isEmpty()) return;
                ArrayList<Channel> list = new ArrayList<>(channels);
                Channel chosen = list.get(new Random().nextInt(list.size()));
                Channel updated = new Channel(chosen.getUuid(), chosen.getCreator(), chosen.getName() + "_upd");
                MessageAppMock.this.mDataManager.sendChannel(updated);
            }
        });

        JLabel fileLabel = new JLabel("Files");

        Button sendUserButton = new Button("Write User");
        sendUserButton.setPreferredSize(new Dimension(100, 50));
        sendUserButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                MessageAppMock.this.writeUser();
            }
        });

        Button sendMessageButton = new Button("Write Message");
        sendMessageButton.setPreferredSize(new Dimension(100, 50));
        sendMessageButton.addActionListener(a -> MessageAppMock.this.writeMessage());

        Button sendChannelButton = new Button("Write Channel");
        sendChannelButton.setPreferredSize(new Dimension(100, 50));
        sendChannelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                MessageAppMock.this.writeChannel();
            }
        });

        this.mFrame.add(dbLabel, new GridBagConstraints(0, 0, 3, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        this.mFrame.add(addUserButton, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(addMessageButton, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(addChannelButton, new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        this.mFrame.add(fileLabel, new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(15, 5, 0, 5), 0, 0));
        this.mFrame.add(sendUserButton, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(sendMessageButton, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(sendChannelButton, new GridBagConstraints(2, 3, 1, 1, 1, 1, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        this.mFrame.add(removeUserButton, new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(removeMessageButton, new GridBagConstraints(1, 5, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(removeChannelButton, new GridBagConstraints(2, 5, 1, 1, 1, 1, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        this.mFrame.add(updateUserButton, new GridBagConstraints(0, 6, 1, 1, 1, 1, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(updateMessageButton, new GridBagConstraints(1, 6, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.mFrame.add(updateChannelButton, new GridBagConstraints(2, 6, 1, 1, 1, 1, GridBagConstraints.EAST,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }

    protected void addUserInDatabase() {
        User newUser = this.generateUser();

        this.mDbConnector.addUser(newUser);
    }

    protected void writeUser() {
        User newUser = this.generateUser();

        this.mDataManager.sendUser(newUser);
    }

    protected User generateUser() {
        int randomInt = new Random().nextInt(99999);
        String userName = "MockUser" + randomInt;
        User newUser = new User(UUID.randomUUID(), userName, "This_Is_Not_A_Password", userName);

        return newUser;
    }

    protected void addMessageInDatabase() {
        Message newMessage = this.generateMessage();

        this.mDbConnector.addMessage(newMessage);
    }

    protected void writeMessage() {
        Message newMessage = this.generateMessage();

        this.mDataManager.sendMessage(newMessage);
    }

    protected void addChannelInDatabase() {
        Channel newChannel = this.generateChannel();

        this.mDbConnector.addChannel(newChannel);
    }

    protected void writeChannel() {
        Channel newChannel = this.generateChannel();

        this.mDataManager.sendChannel(newChannel);
    }

    protected Message generateMessage() {
        if (this.mDataManager.getUsers().isEmpty()) {
            this.addUserInDatabase();
        }

        int userIndex = new Random().nextInt(this.mDataManager.getUsers().size());
        User randomUser = new ArrayList<>(this.mDataManager.getUsers()).get(Math.max(0, userIndex - 1));

        Message newMessage = new Message(randomUser, Constants.UNKNONWN_USER_UUID, "Message fictif!! #Mock #test ;)");

        return newMessage;
    }

    protected Channel generateChannel() {
        if (this.mDataManager.getUsers().isEmpty()) {
            this.addUserInDatabase();
        }

        int userIndex = new Random().nextInt(this.mDataManager.getUsers().size());
        User randomUser = new ArrayList<>(this.mDataManager.getUsers()).get(Math.max(0, userIndex - 1));

        Channel newChannel = new Channel(randomUser, "Canal fictif");

        return newChannel;
    }
}
