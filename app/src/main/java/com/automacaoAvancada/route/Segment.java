package com.automacaoAvancada.route;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Segment implements Parcelable {
    private LatLng start;
    private String instruction;
    private int length;
    private double distance;

    /* Maneuver instructions */
    private String maneuver;
    public Segment() {
    }

    private Segment(Parcel in) {
        start = in.readParcelable(LatLng.class.getClassLoader());
        instruction = in.readString();
        length = in.readInt();
        distance = in.readDouble();
        maneuver = in.readString();
    }


    public void setInstruction(final String turn) {
        this.instruction = turn;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setPoint(final LatLng point) {
        start = point;
    }

    public LatLng startPoint() {
        return start;
    }

    public Segment copy() {
        final Segment copy = new Segment();
        copy.start = start;
        copy.instruction = instruction;
        copy.length = length;
        copy.distance = distance;
        copy.maneuver = maneuver;
        return copy;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setManeuver(String man) {
        maneuver = man;
    }

    public String getManeuver() {
        return maneuver;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(start, flags);
        dest.writeString(instruction);
        dest.writeInt(length);
        dest.writeDouble(distance);
        dest.writeString(maneuver);
    }

    public static final Creator<Segment> CREATOR = new Creator<Segment>() {
        @Override
        public Segment createFromParcel(Parcel in) {
            return new Segment(in);
        }

        @Override
        public Segment[] newArray(int size) {
            return new Segment[size];
        }
    };
}
