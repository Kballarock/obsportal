package by.obs.portal.validation.view;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence(value = {ErrorSequence.First.class, ErrorSequence.Second.class, ErrorSequence.Third.class, Default.class})
public interface ErrorSequence {

    interface First {
    }

    interface Second {
    }

    interface Third {
    }
}