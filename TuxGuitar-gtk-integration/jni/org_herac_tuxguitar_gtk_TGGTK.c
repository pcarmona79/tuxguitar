#include <gtk/gtk.h>
#include "org_herac_tuxguitar_gtk_TGGTK.h"


static JavaVM* JNI_JVM = NULL;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
  JNI_JVM = vm;
  return JNI_VERSION_1_4;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createHeaderBar(JNIEnv* env, jclass that)
{
    GtkWidget *bar = gtk_header_bar_new();
    gtk_header_bar_set_show_close_button(GTK_HEADER_BAR(bar), TRUE);
    return (jlong) bar;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createButtonBox(JNIEnv* env, jclass that)
{
    GtkWidget *button_box = gtk_button_box_new(GTK_ORIENTATION_HORIZONTAL);
    gtk_button_box_set_layout(GTK_BUTTON_BOX(button_box), GTK_BUTTONBOX_EXPAND);
    gtk_box_set_homogeneous(GTK_BOX(button_box), FALSE);
    return (jlong) button_box;
}

static gboolean scrolled_configure(GtkWidget *widget, GdkRectangle *rect, gpointer data)
{
  GtkAdjustment *adjustment = gtk_scrolled_window_get_hadjustment(GTK_SCROLLED_WINDOW(widget));

  int alignment = GPOINTER_TO_INT(g_object_get_data(G_OBJECT(widget), "alignment"));
  if (alignment == GTK_ALIGN_END) {
    gtk_adjustment_set_value(adjustment, gtk_adjustment_get_upper(adjustment));
  }

  GtkWidget *container = gtk_bin_get_child(GTK_BIN(widget));
  if (container != NULL && GTK_IS_VIEWPORT(container)) {
    container = gtk_bin_get_child(GTK_BIN(container));
  }

  if (container != NULL && GTK_IS_CONTAINER(container)) {
    int outerX = gtk_adjustment_get_value(adjustment);
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

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createHidingScrolledWindow(JNIEnv* env, jclass that, jint alignment)
{
  GtkWidget *scrolled = gtk_scrolled_window_new(NULL, NULL);
  gtk_scrolled_window_set_kinetic_scrolling(GTK_SCROLLED_WINDOW(scrolled), FALSE);
  gtk_scrolled_window_set_policy(GTK_SCROLLED_WINDOW(scrolled), GTK_POLICY_EXTERNAL, GTK_POLICY_NEVER);
  gtk_widget_set_hexpand(scrolled, TRUE);
  g_object_set_data(G_OBJECT(scrolled), "alignment", GINT_TO_POINTER(alignment));
  g_signal_connect(scrolled, "size-allocate", G_CALLBACK(scrolled_configure), NULL);
  return (jlong) scrolled;
}

static const char *TG_ACTION_PREFIX = "tg";

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuButton(JNIEnv* env, jclass that, jlong box)
{
  GtkWidget *button = gtk_menu_button_new();
  gtk_button_set_image(GTK_BUTTON(button), gtk_image_new_from_icon_name("open-menu-symbolic", GTK_ICON_SIZE_BUTTON));
  gtk_widget_set_can_focus(button, FALSE);

  GSimpleActionGroup *actions = g_simple_action_group_new();
  gtk_widget_insert_action_group(button, TG_ACTION_PREFIX, G_ACTION_GROUP(actions));
  g_object_unref(actions);

  GtkWidget *popover = gtk_popover_menu_new();
  gtk_menu_button_set_popover(GTK_MENU_BUTTON(button), popover);
  gtk_container_add(GTK_CONTAINER(popover), GTK_WIDGET(box));
  /*
  gtk_container_child_set(GTK_CONTAINER(popover), GTK_WIDGET(box), "submenu", "main", NULL);
  g_object_set(G_OBJECT(popover), "visible-submenu", "main", NULL);
  */

  return (jlong) button;
}

static void model_button_cleanup(GtkWidget* button, gpointer data)
{
  GAction *action = G_ACTION(g_object_get_data(G_OBJECT(button), "action"));
  g_action_map_remove_action(G_ACTION_MAP(data), g_action_get_name(action));
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuActionItem(JNIEnv* env, jclass that, jlong menu, jstring nameStr)
{
  const char *name = (*env)->GetStringUTFChars(env, nameStr, NULL);

  GActionGroup *actions = gtk_widget_get_action_group(GTK_WIDGET(menu), TG_ACTION_PREFIX);
  GSimpleAction *action = g_simple_action_new(name, NULL);
  g_action_map_add_action(G_ACTION_MAP(actions), G_ACTION(action));
  g_object_unref(action);

  GtkWidget *button = gtk_model_button_new();
  g_autoptr(GString) prefixed_name = g_string_new(TG_ACTION_PREFIX);
  g_string_append(prefixed_name, ".");
  g_string_append(prefixed_name, name);
  gtk_actionable_set_action_name(GTK_ACTIONABLE(button), prefixed_name->str);
  g_object_set_data(G_OBJECT(button), "action", action);
  g_signal_connect(G_OBJECT(button), "destroy", G_CALLBACK(model_button_cleanup), actions);
  return (jlong) button;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuCheckboxItem(JNIEnv* env, jclass that, jlong menu, jstring nameStr)
{
  jlong button = Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuActionItem(env, that, menu, nameStr);
  g_object_set(G_OBJECT(button), "role", GTK_BUTTON_ROLE_CHECK, NULL);
  return button;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuRadioItem(JNIEnv* env, jclass that, jlong menu, jstring nameStr)
{
  jlong button = Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuActionItem(env, that, menu, nameStr);
  g_object_set(G_OBJECT(button), "role", GTK_BUTTON_ROLE_RADIO, NULL);
  return button;
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuSubMenuItem(JNIEnv* env, jclass that, jlong menu, jstring menuName, jlong box)
{
  const char *name = (*env)->GetStringUTFChars(env, menuName, NULL);

  GtkWidget *button = gtk_model_button_new();
  g_object_set(G_OBJECT(button), "menu-name", (*env)->GetStringUTFChars(env, menuName, NULL), NULL);

  GtkPopoverMenu *popover = GTK_POPOVER_MENU(gtk_menu_button_get_popover(GTK_MENU_BUTTON(menu)));
  gtk_container_add(GTK_CONTAINER(popover), GTK_WIDGET(box));
  gtk_container_child_set(GTK_CONTAINER(popover), GTK_WIDGET(box), "submenu", name, NULL);

  return (jlong) button;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1menuItemConnectActivated(JNIEnv* env, jclass that, jlong item, jlong callback)
{
  gpointer action = g_object_get_data(G_OBJECT(item), "action");
  g_signal_connect(G_OBJECT(action), "activate", G_CALLBACK(callback), NULL);
}

JNIEXPORT jlong JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1createMenuGoBackItem(JNIEnv* env, jclass that, jstring menuName)
{
  GtkWidget *button = gtk_model_button_new();
  g_object_set(G_OBJECT(button), "menu-name", (*env)->GetStringUTFChars(env, menuName, NULL), "inverted", TRUE, "centered", TRUE, NULL);
  return (jlong) button;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1menuItemSetEnabled(JNIEnv* env, jclass that, jlong object, jboolean enabled)
{
  GAction *action = G_ACTION(g_object_get_data(G_OBJECT(object), "action"));
  if (action == NULL) {
    gtk_widget_set_sensitive(GTK_WIDGET(object), enabled);
  } else {
    g_object_set(G_OBJECT(action), "enabled", enabled, NULL);
  }
}

JNIEXPORT jboolean JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1menuItemGetEnabled(JNIEnv* env, jclass that, jlong object)
{
  GAction *action = G_ACTION(g_object_get_data(G_OBJECT(object), "action"));
  if (action == NULL) {
    return gtk_widget_get_sensitive(GTK_WIDGET(object));
  }
  gboolean enabled;
  g_object_get(G_OBJECT(action), "enabled", &enabled, NULL);
  return enabled;
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1headerBarPackLeft(JNIEnv* env, jclass that, jlong bar, jlong widget)
{
    gtk_header_bar_pack_start(GTK_HEADER_BAR(bar), GTK_WIDGET(widget));
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1headerBarPackCenter(JNIEnv* env, jclass that, jlong bar, jlong widget)
{
    gtk_header_bar_set_custom_title(GTK_HEADER_BAR(bar), GTK_WIDGET(widget));
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1headerBarPackRight(JNIEnv* env, jclass that, jlong bar, jlong widget)
{
    gtk_header_bar_pack_end(GTK_HEADER_BAR(bar), GTK_WIDGET(widget));
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1windowSetHeaderBar(JNIEnv* env, jclass that, jlong window, jlong header)
{
    gtk_window_set_titlebar(GTK_WINDOW(window), GTK_WIDGET(header));
}

JNIEXPORT void JNICALL Java_org_herac_tuxguitar_gtk_TGGTK__1showAll(JNIEnv* env, jclass that, jlong widget)
{
    gtk_widget_show_all(GTK_WIDGET(widget));
}
