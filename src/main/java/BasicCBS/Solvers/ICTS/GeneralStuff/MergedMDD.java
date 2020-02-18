package BasicCBS.Solvers.ICTS.GeneralStuff;

public class MergedMDD {
    private MergedMDDNode start;
    private MergedMDDNode goal;

    public MergedMDDNode getStart() {
        return start;
    }

    public MergedMDDNode getGoal() {
        return goal;
    }

    public void setStart(MergedMDDNode start) {
        this.start = start;
    }

    public void setGoal(MergedMDDNode goal) {
        this.goal = goal;
    }
}
