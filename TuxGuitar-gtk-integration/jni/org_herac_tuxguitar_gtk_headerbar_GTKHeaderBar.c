#include <gtk/gtk.h>
#include "org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar.h"


static JavaVM* JNI_JVM = NULL;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
	JNI_JVM = vm;
	return JNI_VERSION_1_4;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar_createHeaderBar(JNIEnv* env, jclass that)
{
    GtkWidget *bar = gtk_header_bar_new();
    gtk_header_bar_set_show_close_button(GTK_HEADER_BAR(bar), TRUE);
    return (jlong) bar;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar_createButtonBox(JNIEnv* env, jclass that)
{
    GtkWidget *button_box = gtk_button_box_new(GTK_ORIENTATION_HORIZONTAL);
    gtk_button_box_set_layout(GTK_BUTTON_BOX(button_box), GTK_BUTTONBOX_EXPAND);
    gtk_box_set_homogeneous(GTK_BOX(button_box), FALSE);
    return (jlong) button_box;
}

static gboolean scrolled_align_right(GtkWidget *widget, GdkRectangle *rect, gpointer data) {
  GtkAdjustment *adjustment = gtk_scrolled_window_get_hadjustment(GTK_SCROLLED_WINDOW(widget));
  gtk_adjustment_set_value(adjustment, gtk_adjustment_get_upper(adjustment));
  return FALSE;
}

static gboolean scrolled_configure(GtkWidget *widget, GdkRectangle *rect, gpointer data) {
  GtkWidget *container = gtk_bin_get_child(GTK_BIN(widget));
  if (container != NULL && GTK_IS_VIEWPORT(container)) {
    container = gtk_bin_get_child(GTK_BIN(container));
  }
  if (container != NULL && GTK_IS_CONTAINER(container)) {
    int outerX = gtk_adjustment_get_value(gtk_scrolled_window_get_hadjustment(GTK_SCROLLED_WINDOW(widget)));
    g_autoptr(GList) children = gtk_container_get_children(GTK_CONTAINER(container));
    GtkAllocation allocation;
    for (; children != NULL; children = children->next) {
      gtk_widget_get_allocation(children->data, &allocation);
      gboolean outside = allocation.x < outerX || allocation.x + allocation.width > outerX + rect->width;
      gtk_widget_set_visible(children->data, !outside);
    }
  }
  return FALSE;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar_createHidingScrolledWindow(JNIEnv* env, jclass that, jint alignment) {
  GtkWidget *scrolled = gtk_scrolled_window_new(NULL, NULL);
  gtk_scrolled_window_set_kinetic_scrolling(GTK_SCROLLED_WINDOW(scrolled), FALSE);
  gtk_scrolled_window_set_policy(GTK_SCROLLED_WINDOW(scrolled), GTK_POLICY_EXTERNAL, GTK_POLICY_NEVER);
  gtk_widget_set_hexpand(scrolled, TRUE);
  if (alignment == GTK_ALIGN_END) {
    g_signal_connect(scrolled, "size-allocate", G_CALLBACK(scrolled_align_right), NULL);
  }
  g_signal_connect(scrolled, "size-allocate", G_CALLBACK(scrolled_configure), NULL);
  return (jlong) scrolled;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar_packLeft(JNIEnv* env, jclass that, jlong bar, jlong widget)
{
    gtk_header_bar_pack_start(GTK_HEADER_BAR(bar), GTK_WIDGET(widget));
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar_packCenter(JNIEnv* env, jclass that, jlong bar, jlong widget)
{
    gtk_header_bar_set_custom_title(GTK_HEADER_BAR(bar), GTK_WIDGET(widget));
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar_packRight(JNIEnv* env, jclass that, jlong bar, jlong widget)
{
    gtk_header_bar_pack_end(GTK_HEADER_BAR(bar), GTK_WIDGET(widget));
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_headerbar_GTKHeaderBar_setHeaderBar(JNIEnv* env, jclass that, jlong window, jlong header)
{
    gtk_widget_show_all(GTK_WIDGET(header));
    gtk_window_set_titlebar(GTK_WINDOW(window), GTK_WIDGET(header));
}
